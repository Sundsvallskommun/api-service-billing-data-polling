package se.sundsvall.billingdatapolling.service.mapper;

import static generated.se.sundsvall.billingpreprocessor.Status.APPROVED;
import static generated.se.sundsvall.billingpreprocessor.Type.INTERNAL;
import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.billingdatapolling.service.util.SmexUtils.filterByReferenceCode;
import static se.sundsvall.dept44.util.DateUtils.toOffsetDateTimeWithLocalOffset;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.zalando.problem.Problem;

import generated.se.sundsvall.billingpreprocessor.AccountInformation;
import generated.se.sundsvall.billingpreprocessor.BillingRecord;
import generated.se.sundsvall.billingpreprocessor.Invoice;
import generated.se.sundsvall.billingpreprocessor.InvoiceRow;
import generated.se.sundsvall.billingpreprocessor.Type;
import generated.se.sundsvall.oep.getinstance.FlowInstanceHeader;
import generated.se.sundsvall.oep.getinstance.FlowInstanceValue;
import generated.se.sundsvall.oep.getinstance.FlowInstanceValues;
import generated.se.sundsvall.oep.getinstance.FlowInstanceValuesInternalContactData;
import generated.se.sundsvall.smex.skreferensnummer.SKReferensNummer;
import se.sundsvall.billingdatapolling.integration.db.model.AccessCardEntity;

public class AccessCardMapper {

	// Constants
	static final float TOTAL_AMOUNT_WITH_PHOTO = 200;
	static final float TOTAL_AMOUNT_WITHOUT_PHOTO = 150;
	static final Type TYPE = INTERNAL;
	static final String CATEGORY = "ACCESS_CARD";
	static final String APPROVED_BY = "OeP";
	static final String CUSTOMER_ID = "16";
	static final String SUBACCOUNT = "936100";
	static final String ACTIVITY = "5247000";
	static final String DEPARTMENT = "910300";
	static final String COST_CENTER = "1620000";
	static final String DESCRIPTIONS_ROW_1 = "Ordernummer: %s";
	static final String DESCRIPTIONS_ROW_2 = "Beställare: %s %s";
	static final String DESCRIPTIONS_ROW_3 = "Användare: %s %s %s";
	static final String DESCRIPTIONS_ROW_4_WITH_PHOTO = "Passerkort med foto";
	static final String DESCRIPTIONS_ROW_4_WITHOUT_PHOTO = "Passerkort utan foto";
	static final String CASE_TYPE_TO_PROCESS = "Beställ nytt passerkort";

	// Error message
	static final String ERROR_MESSAGE_OBJECTS_MISSING = "Mapping to BillingRecord not possible. One or more of the input objects was null.";

	private AccessCardMapper() {}

	//////////////////////////////////////////////////////////////////
	/// FlowInstance to DB mapping operations
	//////////////////////////////////////////////////////////////////

	public static Integer toFlowInstanceId(final generated.se.sundsvall.oep.getinstances.FlowInstance flowInstance) {
		return Optional.ofNullable(flowInstance)
			.map(generated.se.sundsvall.oep.getinstances.FlowInstance::getFlowInstanceID)
			.orElse(null);
	}

	public static AccessCardEntity toAccessCardEntity(final generated.se.sundsvall.oep.getinstance.FlowInstance flowInstance, final List<SKReferensNummer> skReferensNummerList) {
		if (isAccessCardOrder(flowInstance)) {
			final var referenceCode = normalizeSpace(toReferenceCode(flowInstance));
			final var skReferensNummer = filterByReferenceCode(skReferensNummerList, referenceCode);

			return AccessCardEntity.create()
				.withFirstName(normalizeSpace(toFirstName(flowInstance)))
				.withLastName(normalizeSpace(toLastName(flowInstance)))
				.withUsername(normalizeSpace(toUsername(flowInstance)))
				.withFlowInstanceId(normalizeSpace(toFlowInstanceId(flowInstance)))
				.withPhoto(toPhoto(flowInstance))
				.withReferenceCode(referenceCode)
				.withReferenceCodeId(String.valueOf(skReferensNummer.getREFKODID()))
				.withReferenceName(normalizeSpace(skReferensNummer.getANVNAMN()))
				.withPosted(toPosted(flowInstance));
		}
		return null;
	}

	private static String toFirstName(final generated.se.sundsvall.oep.getinstance.FlowInstance flowInstance) {
		return Optional.ofNullable(flowInstance)
			.map(generated.se.sundsvall.oep.getinstance.FlowInstance::getValues)
			.map(FlowInstanceValues::getInternalContactData)
			.map(FlowInstanceValuesInternalContactData::getFirstname)
			.orElse(null);
	}

	private static String toLastName(final generated.se.sundsvall.oep.getinstance.FlowInstance flowInstance) {
		return Optional.ofNullable(flowInstance)
			.map(generated.se.sundsvall.oep.getinstance.FlowInstance::getValues)
			.map(FlowInstanceValues::getInternalContactData)
			.map(FlowInstanceValuesInternalContactData::getLastname)
			.orElse(null);
	}

	private static String toUsername(final generated.se.sundsvall.oep.getinstance.FlowInstance flowInstance) {
		return Optional.ofNullable(flowInstance)
			.map(generated.se.sundsvall.oep.getinstance.FlowInstance::getValues)
			.map(FlowInstanceValues::getInternalContactData)
			.map(FlowInstanceValuesInternalContactData::getUsername)
			.orElse(null);
	}

	private static String toFlowInstanceId(final generated.se.sundsvall.oep.getinstance.FlowInstance flowInstance) {
		return Optional.ofNullable(flowInstance)
			.map(generated.se.sundsvall.oep.getinstance.FlowInstance::getHeader)
			.map(FlowInstanceHeader::getFlowInstanceID)
			.map(String::valueOf)
			.orElse(null);
	}

	private static boolean toPhoto(final generated.se.sundsvall.oep.getinstance.FlowInstance flowInstance) {
		return Optional.ofNullable(flowInstance)
			.map(generated.se.sundsvall.oep.getinstance.FlowInstance::getValues)
			.map(FlowInstanceValues::getAccessCardPhoto)
			.isPresent();
	}

	private static OffsetDateTime toPosted(final generated.se.sundsvall.oep.getinstance.FlowInstance flowInstance) {
		final var dateTime = Optional.ofNullable(flowInstance)
			.map(generated.se.sundsvall.oep.getinstance.FlowInstance::getHeader)
			.map(FlowInstanceHeader::getPosted)
			.orElse(null);

		if (nonNull(dateTime)) {
			return toOffsetDateTimeWithLocalOffset(LocalDateTime.parse(dateTime, ISO_LOCAL_DATE_TIME)).withNano(0);
		}

		return null;
	}

	private static boolean isAccessCardOrder(final generated.se.sundsvall.oep.getinstance.FlowInstance flowInstance) {
		return Optional.ofNullable(flowInstance)
			.map(generated.se.sundsvall.oep.getinstance.FlowInstance::getValues)
			.map(FlowInstanceValues::getCaseType)
			.filter(caseType -> CASE_TYPE_TO_PROCESS.equals(caseType.getValue()))
			.isPresent();
	}

	private static String toReferenceCode(final generated.se.sundsvall.oep.getinstance.FlowInstance flowInstance) {
		final var refCodeValue = Optional.ofNullable(flowInstance)
			.map(generated.se.sundsvall.oep.getinstance.FlowInstance::getValues)
			.map(FlowInstanceValues::getReferenceCode)
			.map(FlowInstanceValue::getValue)
			.map(String::trim)
			.orElse(null);

		if (nonNull(refCodeValue)) {
			final var pattern = Pattern.compile("^(.*)\s-\s(.*)");
			final var matcher = pattern.matcher(refCodeValue);
			if (matcher.find()) {
				return matcher.group(1);
			}
		}
		return null;
	}

	//////////////////////////////////////////////////////////////////
	/// BillingPreProcessor mapping operations
	//////////////////////////////////////////////////////////////////

	public static BillingRecord toBillingRecord(final AccessCardEntity accessCardEntity) {

		if (isNull(accessCardEntity)) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, ERROR_MESSAGE_OBJECTS_MISSING);
		}

		return new BillingRecord()
			.category(CATEGORY)
			.type(TYPE)
			.status(APPROVED)
			.approvedBy(APPROVED_BY)
			.invoice(new Invoice()
				.customerId(CUSTOMER_ID)
				.ourReference(accessCardEntity.getReferenceName())
				.referenceId(accessCardEntity.getReferenceCode())
				.totalAmount(toTotalAmount(accessCardEntity))
				.invoiceRows(toInvoiceRows(accessCardEntity)));
	}

	private static List<InvoiceRow> toInvoiceRows(final AccessCardEntity accessCardEntity) {
		return List.of(
			new InvoiceRow()
				.addDescriptionsItem(normalizeSpace(format(DESCRIPTIONS_ROW_1, accessCardEntity.getFlowInstanceId())))
				.quantity(0),
			new InvoiceRow()
				.addDescriptionsItem(normalizeSpace(format(DESCRIPTIONS_ROW_2, accessCardEntity.getReferenceName(), accessCardEntity.getReferenceCode())))
				.quantity(0),
			new InvoiceRow()
				.addDescriptionsItem(normalizeSpace(format(DESCRIPTIONS_ROW_3, accessCardEntity.getFirstName(), accessCardEntity.getLastName(), accessCardEntity.getUsername())))
				.quantity(0),
			new InvoiceRow()
				.addDescriptionsItem(accessCardEntity.hasPhoto() ? DESCRIPTIONS_ROW_4_WITH_PHOTO : DESCRIPTIONS_ROW_4_WITHOUT_PHOTO)
				.quantity(1)
				.totalAmount(toTotalAmount(accessCardEntity))
				.accountInformation(new AccountInformation()
					.subaccount(SUBACCOUNT)
					.activity(ACTIVITY)
					.department(DEPARTMENT)
					.costCenter(COST_CENTER)));
	}

	private static float toTotalAmount(final AccessCardEntity accessCardEntity) {
		if (accessCardEntity.hasPhoto()) {
			return TOTAL_AMOUNT_WITH_PHOTO;
		}

		return TOTAL_AMOUNT_WITHOUT_PHOTO;
	}
}
