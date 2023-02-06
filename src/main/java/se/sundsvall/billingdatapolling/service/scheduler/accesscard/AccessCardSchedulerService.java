package se.sundsvall.billingdatapolling.service.scheduler.accesscard;

import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;
import static se.sundsvall.billingdatapolling.integration.db.model.enums.Status.FAILED;
import static se.sundsvall.billingdatapolling.integration.db.model.enums.Status.PROCESSED;
import static se.sundsvall.billingdatapolling.integration.db.model.enums.Status.UNPROCESSED;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.toAccessCardEntity;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.toBillingRecord;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import generated.se.sundsvall.oep.getinstance.FlowInstance;
import se.sundsvall.billingdatapolling.integration.billingpreprocessor.BillingPreProcessorClient;
import se.sundsvall.billingdatapolling.integration.db.AccessCardRepository;
import se.sundsvall.billingdatapolling.integration.db.model.AccessCardEntity;
import se.sundsvall.billingdatapolling.integration.oep.OepGetInstanceClient;
import se.sundsvall.billingdatapolling.integration.smex.SmexClient;
import se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper;
import se.sundsvall.billingdatapolling.service.messaging.MessagingService;
import se.sundsvall.billingdatapolling.service.scheduler.AbstractScheduler;

@Service
@ConfigurationProperties("accesscard.scheduler.cron")
public class AccessCardSchedulerService extends AbstractScheduler {

	@Autowired
	private BillingPreProcessorClient billingPreProcessorClient;

	@Autowired
	private AccessCardRepository accessCardRepository;

	@Autowired
	private OepGetInstanceClient oepGetInstanceClient;

	@Autowired
	private SmexClient smexClient;

	@Autowired
	private MessagingService messagingService;

	/**
	 * Executes the polling, according to this algorithm:
	 *
	 * 1. Call OEP to fetch all orders (poll fromDate: last stored postDate, minus 1 day, on orders. pollToDate: now).
	 * 2. Enrich orders with data from SMEX.
	 * 3. Store all orders in DB with status UNPROCESSED.
	 * 4. Fetch all orders from DB with status FAILED and UNPROCESSED.
	 * 5. Send orders to BillingPreProcessor and set status to PROCESSED/FAILED.
	 * 6. If there are failed orders in database. Send mail to configured recipient.
	 */
	@Override
	@Scheduled(cron = "${accesscard.scheduler.cron.expression:-}")
	public void execute() {
		execute(null, null);
	}

	/**
	 * Executes the polling, according to this algorithm:
	 *
	 * 1. Call OEP to fetch all orders (poll fromDate: provided. pollToDate: provided).
	 * 2. Enrich orders with data from SMEX.
	 * 3. Store all orders in DB with status UNPROCESSED.
	 * 4. Fetch all orders from DB with status FAILED and UNPROCESSED.
	 * 5. Send orders to BillingPreProcessor and set status to PROCESSED/FAILED.
	 * 6. If there are failed orders in database. Send mail to configured recipient.
	 *
	 * @param fromDate poll fromDate.
	 * @param toDate   poll toDate.
	 */
	public void execute(final LocalDate fromDate, final LocalDate toDate) {
		preProcess(Optional.ofNullable(fromDate).orElse(generateFromDate()), Optional.ofNullable(toDate).orElse(generateToDate()));
		process();
		postProcess();
	}

	/**
	 * PreProcess:
	 *
	 * 1. Call OEP to fetch all orders (poll fromDate: provided. pollToDate: provided).
	 * 2. Enrich orders with data from SMEX.
	 * 3. Store all orders in DB with status UNPROCESSED.
	 *
	 * @param fromDate poll fromDate.
	 * @param toDate   poll toDate.
	 */
	private void preProcess(final LocalDate fromDate, final LocalDate toDate) {
		Optional.ofNullable(pollFlowInstanceIds(fromDate, toDate)).orElse(emptyList()).stream()
			.map(this::fetchFlowInstance)
			.map(flowInstance -> toAccessCardEntity(flowInstance, smexClient.getSKReferensNummer()))
			.forEach(this::savePolledOrdersInDatabase);
	}

	/**
	 * Process:
	 *
	 * 1. Fetch all orders from DB with status FAILED and UNPROCESSED.
	 * 2. Send orders to BillingPreProcessor and set status to PROCESSED/FAILED.
	 * 3. Save result (failure or success) in DB.
	 */
	private void process() {
		final var accessCardsToProcess = accessCardRepository.findByStatusIn(List.of(FAILED, UNPROCESSED));

		accessCardsToProcess.forEach(accessCard -> {
			try {
				billingPreProcessorClient.createBillingRecord(toBillingRecord(accessCard));
			} catch (final Exception e) {
				e.printStackTrace();
				accessCardRepository.save(accessCard
					.withStatus(FAILED)
					.withStatusMessage(e.getMessage()));
				return;
			}

			accessCardRepository.save(accessCard.withStatus(PROCESSED));
		});
	}

	/**
	 * PostProcess:
	 *
	 * 1. Fetch all failed access card orders from DB.
	 * 2. Send failed access card orders to mail recipient.
	 */
	private void postProcess() {
		final var failedAccessCardList = accessCardRepository.findByStatusIn(List.of(FAILED));
		if (failedAccessCardList.isEmpty()) {
			return;
		}
		messagingService.sendFailureMessage(failedAccessCardList);
	}

	private List<Integer> pollFlowInstanceIds(final LocalDate fromDate, final LocalDate toDate) {
		return Optional.ofNullable(oepGetInstanceClient.getInstancesFilteredByDate(fromDate, toDate)).orElse(emptyList()).stream()
			.map(AccessCardMapper::toFlowInstanceId)
			.filter(Objects::nonNull)
			.toList();
	}

	private FlowInstance fetchFlowInstance(final Integer flowInstanceId) {
		return oepGetInstanceClient.getInstanceByFlowInstanceId(flowInstanceId);
	}

	private void savePolledOrdersInDatabase(final AccessCardEntity accessCard) {
		Optional.ofNullable(accessCard).ifPresent(entity -> {
			// Save only non-existing entities.
			if (accessCardRepository.findByFlowInstanceId(entity.getFlowInstanceId()).isEmpty()) {
				accessCardRepository.save(entity);
			}
		});
	}

	private LocalDate generateFromDate() {
		// Fetch last posted entity (this will be the last date we polled).
		final var lastPostedEntity = accessCardRepository.findFirstByOrderByPostedDesc();
		if (lastPostedEntity.isPresent()) {
			return lastPostedEntity.get().getPosted().minusDays(1).toLocalDate();
		}

		// No poll date was found. Return a LocalDate from the beginning of time.
		return LocalDate.of(1970, 1, 1);
	}

	private LocalDate generateToDate() {
		return now(ZoneId.systemDefault());
	}
}
