package se.sundsvall.billingdatapolling.service.scheduler.accesscard;

import static generated.se.sundsvall.billingpreprocessor.Status.APPROVED;
import static generated.se.sundsvall.billingpreprocessor.Type.INTERNAL;
import static java.time.OffsetDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.billingdatapolling.integration.db.model.enums.Status.FAILED;
import static se.sundsvall.billingdatapolling.integration.db.model.enums.Status.PROCESSED;
import static se.sundsvall.billingdatapolling.integration.db.model.enums.Status.UNPROCESSED;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;

import generated.se.sundsvall.billingpreprocessor.AccountInformation;
import generated.se.sundsvall.billingpreprocessor.BillingRecord;
import generated.se.sundsvall.billingpreprocessor.Invoice;
import generated.se.sundsvall.billingpreprocessor.InvoiceRow;
import generated.se.sundsvall.messaging.EmailRequest;
import generated.se.sundsvall.oep.getinstance.FlowInstanceHeader;
import generated.se.sundsvall.oep.getinstance.FlowInstanceValue;
import generated.se.sundsvall.oep.getinstance.FlowInstanceValues;
import generated.se.sundsvall.oep.getinstance.FlowInstanceValuesAccessCardPhoto;
import generated.se.sundsvall.oep.getinstance.FlowInstanceValuesInternalContactData;
import generated.se.sundsvall.smex.skreferensnummer.SKReferensNummer;
import se.sundsvall.billingdatapolling.integration.billingpreprocessor.BillingPreProcessorClient;
import se.sundsvall.billingdatapolling.integration.db.AccessCardRepository;
import se.sundsvall.billingdatapolling.integration.db.model.AccessCardEntity;
import se.sundsvall.billingdatapolling.integration.oep.OepGetInstanceClient;
import se.sundsvall.billingdatapolling.integration.smex.SmexClient;
import se.sundsvall.billingdatapolling.service.messaging.MessagingService;

@ExtendWith(MockitoExtension.class)
class AccessCardSchedulerServiceTest {

	private static final OffsetDateTime POSTED_DATE = OffsetDateTime.parse("2020-02-03T10:17:30+01:00", ISO_OFFSET_DATE_TIME);

	@Mock
	private BillingPreProcessorClient billingPreProcessorClientMock;

	@Mock
	private AccessCardRepository accessCardRepositoryMock;

	@Mock
	private OepGetInstanceClient oepGetInstanceClientMock;

	@Mock
	private SmexClient smexClientMock;

	@Mock
	private MessagingService messagingServiceMock;

	@InjectMocks
	private AccessCardSchedulerService service;

	@Captor
	private ArgumentCaptor<AccessCardEntity> accessCardEntityCaptor;

	@Captor
	private ArgumentCaptor<BillingRecord> billingRecordCaptor;

	@Captor
	private ArgumentCaptor<EmailRequest> emailRequestCaptor;

	@Test
	void execute() {

		// Setup
		final var flowInstanceId = 1;

		when(accessCardRepositoryMock.findFirstByOrderByPostedDesc()).thenReturn(Optional.of(AccessCardEntity.create().withPosted(POSTED_DATE)));
		when(oepGetInstanceClientMock.getInstancesFilteredByDate(POSTED_DATE.toLocalDate().minusDays(1), LocalDate.now())).thenReturn(generateGetInstancesFlowinstanceList());
		when(oepGetInstanceClientMock.getInstanceByFlowInstanceId(flowInstanceId)).thenReturn(generateGetInstanceFlowinstance(1));
		when(smexClientMock.getSKReferensNummer()).thenReturn(generateSKReferensNummerListWithAnElementWithRefkod("Referencecode1"));
		when(accessCardRepositoryMock.findByFlowInstanceId(String.valueOf(flowInstanceId))).thenReturn(Optional.empty());
		when(accessCardRepositoryMock.findByStatusIn(List.of(FAILED, UNPROCESSED))).thenReturn(generateListOfAccessCardEntity());
		when(accessCardRepositoryMock.findByStatusIn(List.of(FAILED))).thenReturn(emptyList());

		// Call.
		service.execute();

		// Verification.
		verify(accessCardRepositoryMock).findFirstByOrderByPostedDesc();
		verify(oepGetInstanceClientMock).getInstancesFilteredByDate(POSTED_DATE.toLocalDate().minusDays(1), LocalDate.now());
		verify(oepGetInstanceClientMock).getInstanceByFlowInstanceId(flowInstanceId);
		verify(smexClientMock, times(3)).getSKReferensNummer(); // 1 call for every generated.se.sundsvall.oep.getinstances.FlowInstance.
		verify(accessCardRepositoryMock).findByFlowInstanceId(String.valueOf(flowInstanceId));
		verify(accessCardRepositoryMock, times(4)).save(accessCardEntityCaptor.capture()); // 3 calls when created and 1 call when status is set to PROCESSED
		verify(accessCardRepositoryMock).findByStatusIn(List.of(FAILED, UNPROCESSED));
		verify(billingPreProcessorClientMock, times(3)).createBillingRecord(billingRecordCaptor.capture());
		verify(accessCardRepositoryMock).findByStatusIn(List.of(FAILED));
		verifyNoInteractions(messagingServiceMock);

		// Assert captors.
		assertThat(accessCardEntityCaptor.getAllValues())
			.extracting(
				AccessCardEntity::getId,
				AccessCardEntity::getFirstName,
				AccessCardEntity::getLastName,
				AccessCardEntity::getFlowInstanceId,
				AccessCardEntity::getPhoto,
				AccessCardEntity::getPosted,
				AccessCardEntity::getReferenceCode,
				AccessCardEntity::getReferenceCodeId,
				AccessCardEntity::getReferenceName,
				AccessCardEntity::getStatus,
				AccessCardEntity::getUsername)
			.containsExactly(
				// Create record in DB.
				tuple(null, "Firstname1", "Lastname1", "1", true, POSTED_DATE.minusDays(1), "Referencecode1", "666", "SK_USERNAME", null, "Username1"),
				// Update status in DB.
				tuple(1L, "Firstname1", "Lastname1", "1", true, POSTED_DATE.minusDays(1), "Referencecode1", "1", "John Doe 1", PROCESSED, "Username1"),
				tuple(2L, "Firstname2", "Lastname2", "2", true, POSTED_DATE.minusDays(2), "Referencecode2", "2", "John Doe 2", PROCESSED, "Username2"),
				tuple(3L, "Firstname3", "Lastname3", "3", false, POSTED_DATE.minusDays(3), "Referencecode3", "3", "John Doe 3", PROCESSED, "Username3"));

		assertThat(billingRecordCaptor.getAllValues())
			.extracting(
				BillingRecord::getCategory,
				BillingRecord::getType,
				BillingRecord::getStatus,
				BillingRecord::getInvoice)
			.containsExactly(
				tuple("ACCESS_CARD", INTERNAL, APPROVED, new Invoice()
					.customerId("16")
					.ourReference("John Doe 1")
					.referenceId("Referencecode1")
					.totalAmount(200.0f)
					.invoiceRows(List.of(
						new InvoiceRow()
							.quantity(0)
							.descriptions(List.of("Ordernummer: 1")),
						new InvoiceRow()
							.quantity(0)
							.descriptions(List.of("Beställare: John Doe 1 Referencecode1")),
						new InvoiceRow()
							.quantity(0)
							.descriptions(List.of("Användare: Firstname1 Lastname1 Username1")),
						new InvoiceRow()
							.quantity(1)
							.descriptions(List.of("Passerkort med foto"))
							.totalAmount(200.0f)
							.accountInformation(new AccountInformation()
								.costCenter("1620000")
								.subaccount("936100")
								.department("910300")
								.activity("5247000"))))),

				tuple("ACCESS_CARD", INTERNAL, APPROVED, new Invoice()
					.customerId("16")
					.ourReference("John Doe 2")
					.referenceId("Referencecode2")
					.totalAmount(200.0f)
					.invoiceRows(List.of(
						new InvoiceRow()
							.quantity(0)
							.descriptions(List.of("Ordernummer: 2")),
						new InvoiceRow()
							.quantity(0)
							.descriptions(List.of("Beställare: John Doe 2 Referencecode2")),
						new InvoiceRow()
							.quantity(0)
							.descriptions(List.of("Användare: Firstname2 Lastname2 Username2")),
						new InvoiceRow()
							.quantity(1)
							.descriptions(List.of("Passerkort med foto"))
							.totalAmount(200.0f)
							.accountInformation(new AccountInformation()
								.costCenter("1620000")
								.subaccount("936100")
								.department("910300")
								.activity("5247000"))))),

				tuple("ACCESS_CARD", INTERNAL, APPROVED, new Invoice()
					.customerId("16")
					.ourReference("John Doe 3")
					.referenceId("Referencecode3")
					.totalAmount(150.0f)
					.invoiceRows(List.of(
						new InvoiceRow()
							.quantity(0)
							.descriptions(List.of("Ordernummer: 3")),
						new InvoiceRow()
							.quantity(0)
							.descriptions(List.of("Beställare: John Doe 3 Referencecode3")),
						new InvoiceRow()
							.quantity(0)
							.descriptions(List.of("Användare: Firstname3 Lastname3 Username3")),
						new InvoiceRow()
							.quantity(1)
							.descriptions(List.of("Passerkort utan foto"))
							.totalAmount(150.0f)
							.accountInformation(new AccountInformation()
								.costCenter("1620000")
								.subaccount("936100")
								.department("910300")
								.activity("5247000"))))));
	}

	@Test
	void executeWhenFailureOccurs() {

		// Setup
		final var flowInstanceId = 1;
		final var faultyRecords = List.of(AccessCardEntity.create().withId(666L).withStatusMessage("Error LOL :-)"));

		when(accessCardRepositoryMock.findFirstByOrderByPostedDesc()).thenReturn(Optional.of(AccessCardEntity.create().withPosted(POSTED_DATE)));
		when(oepGetInstanceClientMock.getInstancesFilteredByDate(POSTED_DATE.toLocalDate().minusDays(1), LocalDate.now())).thenReturn(generateGetInstancesFlowinstanceList());
		when(oepGetInstanceClientMock.getInstanceByFlowInstanceId(flowInstanceId)).thenReturn(generateGetInstanceFlowinstance(1));
		when(smexClientMock.getSKReferensNummer()).thenReturn(generateSKReferensNummerListWithAnElementWithRefkod("Referencecode1"));
		when(accessCardRepositoryMock.findByFlowInstanceId(String.valueOf(flowInstanceId))).thenReturn(Optional.empty());
		when(accessCardRepositoryMock.findByStatusIn(List.of(FAILED, UNPROCESSED))).thenReturn(generateListOfAccessCardEntity());
		when(accessCardRepositoryMock.findByStatusIn(List.of(FAILED))).thenReturn(faultyRecords);
		doThrow(Problem.valueOf(INTERNAL_SERVER_ERROR, "An error occured")).when(billingPreProcessorClientMock).createBillingRecord(any());

		// Call.
		service.execute();

		// Verification.
		verify(accessCardRepositoryMock).findFirstByOrderByPostedDesc();
		verify(oepGetInstanceClientMock).getInstancesFilteredByDate(POSTED_DATE.toLocalDate().minusDays(1), LocalDate.now());
		verify(oepGetInstanceClientMock).getInstanceByFlowInstanceId(flowInstanceId);
		verify(smexClientMock, times(3)).getSKReferensNummer(); // 1 call for every generated.se.sundsvall.oep.getinstances.FlowInstance.
		verify(accessCardRepositoryMock).findByFlowInstanceId(String.valueOf(flowInstanceId));
		verify(accessCardRepositoryMock, times(4)).save(accessCardEntityCaptor.capture()); // 3 calls when created and 1 call when status is set to PROCESSED
		verify(accessCardRepositoryMock).findByStatusIn(List.of(FAILED, UNPROCESSED));
		verify(billingPreProcessorClientMock, times(3)).createBillingRecord(billingRecordCaptor.capture());
		verify(accessCardRepositoryMock).findByStatusIn(List.of(FAILED));
		verify(messagingServiceMock).sendFailureMessage(faultyRecords);

		// Assert captors.
		assertThat(accessCardEntityCaptor.getAllValues())
			.extracting(
				AccessCardEntity::getId,
				AccessCardEntity::getFirstName,
				AccessCardEntity::getLastName,
				AccessCardEntity::getFlowInstanceId,
				AccessCardEntity::getPhoto,
				AccessCardEntity::getPosted,
				AccessCardEntity::getReferenceCode,
				AccessCardEntity::getReferenceCodeId,
				AccessCardEntity::getReferenceName,
				AccessCardEntity::getStatus,
				AccessCardEntity::getUsername)
			.containsExactly(
				// Create record in DB.
				tuple(null, "Firstname1", "Lastname1", "1", true, POSTED_DATE.minusDays(1), "Referencecode1", "666", "SK_USERNAME", null, "Username1"),
				// Update status in DB.
				tuple(1L, "Firstname1", "Lastname1", "1", true, POSTED_DATE.minusDays(1), "Referencecode1", "1", "John Doe 1", FAILED, "Username1"),
				tuple(2L, "Firstname2", "Lastname2", "2", true, POSTED_DATE.minusDays(2), "Referencecode2", "2", "John Doe 2", FAILED, "Username2"),
				tuple(3L, "Firstname3", "Lastname3", "3", false, POSTED_DATE.minusDays(3), "Referencecode3", "3", "John Doe 3", FAILED, "Username3"));

		assertThat(billingRecordCaptor.getAllValues())
			.extracting(
				BillingRecord::getCategory,
				BillingRecord::getType,
				BillingRecord::getStatus,
				BillingRecord::getInvoice)
			.containsExactly(
				tuple("ACCESS_CARD", INTERNAL, APPROVED, new Invoice()
					.customerId("16")
					.ourReference("John Doe 1")
					.referenceId("Referencecode1")
					.totalAmount(200.0f)
					.invoiceRows(List.of(
						new InvoiceRow()
							.quantity(0)
							.descriptions(List.of("Ordernummer: 1")),
						new InvoiceRow()
							.quantity(0)
							.descriptions(List.of("Beställare: John Doe 1 Referencecode1")),
						new InvoiceRow()
							.quantity(0)
							.descriptions(List.of("Användare: Firstname1 Lastname1 Username1")),
						new InvoiceRow()
							.quantity(1)
							.descriptions(List.of("Passerkort med foto"))
							.totalAmount(200.0f)
							.accountInformation(new AccountInformation()
								.costCenter("1620000")
								.subaccount("936100")
								.department("910300")
								.activity("5247000"))))),

				tuple("ACCESS_CARD", INTERNAL, APPROVED, new Invoice()
					.customerId("16")
					.ourReference("John Doe 2")
					.referenceId("Referencecode2")
					.totalAmount(200.0f)
					.invoiceRows(List.of(
						new InvoiceRow()
							.quantity(0)
							.descriptions(List.of("Ordernummer: 2")),
						new InvoiceRow()
							.quantity(0)
							.descriptions(List.of("Beställare: John Doe 2 Referencecode2")),
						new InvoiceRow()
							.quantity(0)
							.descriptions(List.of("Användare: Firstname2 Lastname2 Username2")),
						new InvoiceRow()
							.quantity(1)
							.descriptions(List.of("Passerkort med foto"))
							.totalAmount(200.0f)
							.accountInformation(new AccountInformation()
								.costCenter("1620000")
								.subaccount("936100")
								.department("910300")
								.activity("5247000"))))),

				tuple("ACCESS_CARD", INTERNAL, APPROVED, new Invoice()
					.customerId("16")
					.ourReference("John Doe 3")
					.referenceId("Referencecode3")
					.totalAmount(150.0f)
					.invoiceRows(List.of(
						new InvoiceRow()
							.quantity(0)
							.descriptions(List.of("Ordernummer: 3")),
						new InvoiceRow()
							.quantity(0)
							.descriptions(List.of("Beställare: John Doe 3 Referencecode3")),
						new InvoiceRow()
							.quantity(0)
							.descriptions(List.of("Användare: Firstname3 Lastname3 Username3")),
						new InvoiceRow()
							.quantity(1)
							.descriptions(List.of("Passerkort utan foto"))
							.totalAmount(150.0f)
							.accountInformation(new AccountInformation()
								.costCenter("1620000")
								.subaccount("936100")
								.department("910300")
								.activity("5247000"))))));
	}

	private generated.se.sundsvall.oep.getinstance.FlowInstance generateGetInstanceFlowinstance(final int flowInstanceId) {
		return new generated.se.sundsvall.oep.getinstance.FlowInstance()
			.header(new FlowInstanceHeader()
				.flowInstanceID(flowInstanceId)
				.posted(POSTED_DATE.minusDays(flowInstanceId).format(ISO_LOCAL_DATE_TIME)))
			.values(new FlowInstanceValues()
				.caseType(new FlowInstanceValue().value("Beställ nytt passerkort"))
				.accessCardPhoto(new FlowInstanceValuesAccessCardPhoto())
				.internalContactData(new FlowInstanceValuesInternalContactData()
					.firstname("Firstname" + flowInstanceId)
					.lastname("Lastname" + flowInstanceId)
					.username("Username" + flowInstanceId))
				.referenceCode(new FlowInstanceValue().value("Referencecode" + flowInstanceId + " - John Doe(TEST Dept44)")));
	}

	private List<generated.se.sundsvall.oep.getinstances.FlowInstance> generateGetInstancesFlowinstanceList() {
		return List.of(
			new generated.se.sundsvall.oep.getinstances.FlowInstance().flowInstanceID(1),
			new generated.se.sundsvall.oep.getinstances.FlowInstance().flowInstanceID(2),
			new generated.se.sundsvall.oep.getinstances.FlowInstance().flowInstanceID(3));
	}

	private List<AccessCardEntity> generateListOfAccessCardEntity() {
		return List.of(
			new AccessCardEntity()
				.withCreated(now())
				.withFirstName("Firstname1")
				.withLastName("Lastname1")
				.withFlowInstanceId("1")
				.withId(1L)
				.withModified(now())
				.withPhoto(true)
				.withPosted(POSTED_DATE.minusDays(1))
				.withReferenceCode("Referencecode1")
				.withReferenceCodeId("1")
				.withReferenceName("John Doe 1")
				.withStatus(UNPROCESSED)
				.withUsername("Username1"),
			new AccessCardEntity()
				.withCreated(now())
				.withFirstName("Firstname2")
				.withLastName("Lastname2")
				.withFlowInstanceId("2")
				.withId(2L)
				.withModified(now())
				.withPhoto(true)
				.withPosted(POSTED_DATE.minusDays(2))
				.withReferenceCode("Referencecode2")
				.withReferenceCodeId("2")
				.withReferenceName("John Doe 2")
				.withStatus(UNPROCESSED)
				.withUsername("Username2"),
			new AccessCardEntity()
				.withCreated(now())
				.withFirstName("Firstname3")
				.withLastName("Lastname3")
				.withFlowInstanceId("3")
				.withId(3L)
				.withModified(now())
				.withPhoto(false)
				.withPosted(POSTED_DATE.minusDays(3))
				.withReferenceCode("Referencecode3")
				.withReferenceCodeId("3")
				.withReferenceName("John Doe 3")
				.withStatus(UNPROCESSED)
				.withUsername("Username3"));
	}

	private List<SKReferensNummer> generateSKReferensNummerListWithAnElementWithRefkod(final String referenceCode) {
		return List.of(
			new SKReferensNummer()
				.ANV_NAMN("DUMMY-1")
				.BESKRIVNING("DUMMY-1")
				.REFKOD("DUMMY-1")
				.REFKOD_ID(1L),
			new SKReferensNummer()
				.ANV_NAMN("SK_USERNAME")
				.BESKRIVNING("SK_DESCRIPTION")
				.REFKOD(referenceCode)
				.REFKOD_ID(666L),
			new SKReferensNummer()
				.ANV_NAMN("DUMMY-2")
				.BESKRIVNING("DUMMY-2")
				.REFKOD("DUMMY-2")
				.REFKOD_ID(2L));
	}
}
