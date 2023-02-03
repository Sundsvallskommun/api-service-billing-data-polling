package se.sundsvall.billingdatapolling.service.mapper;

import static generated.se.sundsvall.billingpreprocessor.Status.APPROVED;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.time.OffsetDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.billingdatapolling.integration.db.model.enums.Status.UNPROCESSED;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.ACTIVITY;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.APPROVED_BY;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.CASE_TYPE_TO_PROCESS;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.CATEGORY;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.COST_CENTER;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.CUSTOMER_ID;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.DEPARTMENT;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.DESCRIPTIONS_ROW_1;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.DESCRIPTIONS_ROW_2;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.DESCRIPTIONS_ROW_3;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.DESCRIPTIONS_ROW_4_WITHOUT_PHOTO;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.DESCRIPTIONS_ROW_4_WITH_PHOTO;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.ERROR_MESSAGE_OBJECTS_MISSING;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.SUBACCOUNT;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.TOTAL_AMOUNT_WITHOUT_PHOTO;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.TOTAL_AMOUNT_WITH_PHOTO;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.TYPE;
import static se.sundsvall.dept44.util.DateUtils.toOffsetDateTimeWithLocalOffset;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.zalando.problem.ThrowableProblem;

import generated.se.sundsvall.billingpreprocessor.AccountInformation;
import generated.se.sundsvall.billingpreprocessor.InvoiceRow;
import generated.se.sundsvall.oep.getinstance.FlowInstanceHeader;
import generated.se.sundsvall.oep.getinstance.FlowInstanceValue;
import generated.se.sundsvall.oep.getinstance.FlowInstanceValues;
import generated.se.sundsvall.oep.getinstance.FlowInstanceValuesAccessCardPhoto;
import generated.se.sundsvall.oep.getinstance.FlowInstanceValuesInternalContactData;
import generated.se.sundsvall.smex.skreferensnummer.SKReferensNummer;
import se.sundsvall.billingdatapolling.integration.db.model.AccessCardEntity;

class AccessCardMapperTest {

	// AccessCardEntity values.
	private static final Long ID = 666L;
	private static final String FIRST_NAME = "John";
	private static final String LAST_NAME = "Doe";
	private static final String FLOW_INSTANCE_ID = "flowInstanceId";
	private static final String REFERENCE_CODE = "referenceCode";
	private static final String REFERENCE_CODE_ID = "referenceCodeId";
	private static final String REFERENCE_NAME = "referenceName";
	private static final String USERNAME = "username";
	private static final OffsetDateTime POSTED = toOffsetDateTimeWithLocalOffset(LocalDateTime.parse("2022-12-22T10:14:10", ISO_LOCAL_DATE_TIME));

	// SKReferensNummer values.
	private static final String ANV_NAMN = "anv_namn";
	private static final String BESKRIVNING = "beskrivning";
	private static final String REFKOD = "2ABC5";
	private static final Long REFKOD_ID = 1337L;

	// generated.se.sundsvall.oep.getinstances.FlowInstance values.
	private static final String GET_INSTANCES_FLOW_INSTANCE_ID = "666";

	// generated.se.sundsvall.oep.getinstance.FlowInstance values.
	private static final String GET_INSTANCE_FLOW_INSTANCE_ID = "667";
	private static final String GET_INSTANCE_FIRSTNAME = "Firstname";
	private static final String GET_INSTANCE_LASTNAME = "Lastname";
	private static final String GET_INSTANCE_USERNAME = "Username";
	private static final String GET_INSTANCE_REFERENCE_CODE = " 2ABC5 - John Doe (Some department) ";
	private static final String GET_INSTANCE_EXPECTED_REFERENCE_CODE = "2ABC5";
	private static final String GET_INSTANCE_POSTED = "2022-12-22T10:14:10"; // Same as POSTED above.

	@Test
	void toBillingRecordWithPhoto() {

		// Setup
		final var withPhoto = true;
		final var accessCardEntity = createAccessCardEntity(withPhoto);

		// Call
		final var result = AccessCardMapper.toBillingRecord(accessCardEntity);

		// Verification
		assertThat(result).isNotNull();
		assertThat(result.getApprovedBy()).isEqualTo(APPROVED_BY);
		assertThat(result.getCategory()).isEqualTo(CATEGORY);
		assertThat(result.getType()).isEqualTo(TYPE);
		assertThat(result.getStatus()).isEqualTo(APPROVED);
		assertThat(result.getInvoice()).isNotNull();
		assertThat(result.getInvoice().getCustomerId()).isEqualTo(CUSTOMER_ID);
		assertThat(result.getInvoice().getOurReference()).isEqualTo(REFERENCE_NAME);
		assertThat(result.getInvoice().getReferenceId()).isEqualTo(REFERENCE_CODE);
		assertThat(result.getInvoice().getTotalAmount()).isEqualTo(TOTAL_AMOUNT_WITH_PHOTO);
		assertThat(result.getInvoice().getInvoiceRows()).hasSize(4).containsExactly(
			new InvoiceRow()
				.quantity(0)
				.descriptions(List.of(format(DESCRIPTIONS_ROW_1, FLOW_INSTANCE_ID))),
			new InvoiceRow()
				.quantity(0)
				.descriptions(List.of(format(DESCRIPTIONS_ROW_2, REFERENCE_NAME, REFERENCE_CODE))),
			new InvoiceRow()
				.quantity(0)
				.descriptions(List.of(format(DESCRIPTIONS_ROW_3, FIRST_NAME, LAST_NAME, USERNAME))),
			new InvoiceRow()
				.quantity(1)
				.totalAmount(TOTAL_AMOUNT_WITH_PHOTO)
				.descriptions(List.of(format(DESCRIPTIONS_ROW_4_WITH_PHOTO, FIRST_NAME, LAST_NAME, USERNAME)))
				.accountInformation(new AccountInformation()
					.activity(ACTIVITY)
					.costCenter(COST_CENTER)
					.department(DEPARTMENT)
					.subaccount(SUBACCOUNT)));
	}

	@Test
	void toBillingRecordWithNoPhoto() {

		// Setup
		final var withPhoto = false;
		final var accessCardEntity = createAccessCardEntity(withPhoto);

		// Call
		final var result = AccessCardMapper.toBillingRecord(accessCardEntity);

		// Verification
		assertThat(result).isNotNull();
		assertThat(result.getApprovedBy()).isEqualTo(APPROVED_BY);
		assertThat(result.getCategory()).isEqualTo(CATEGORY);
		assertThat(result.getType()).isEqualTo(TYPE);
		assertThat(result.getStatus()).isEqualTo(APPROVED);
		assertThat(result.getInvoice()).isNotNull();
		assertThat(result.getInvoice().getCustomerId()).isEqualTo(CUSTOMER_ID);
		assertThat(result.getInvoice().getOurReference()).isEqualTo(REFERENCE_NAME);
		assertThat(result.getInvoice().getReferenceId()).isEqualTo(REFERENCE_CODE);
		assertThat(result.getInvoice().getTotalAmount()).isEqualTo(TOTAL_AMOUNT_WITHOUT_PHOTO);
		assertThat(result.getInvoice().getInvoiceRows()).hasSize(4).containsExactly(
			new InvoiceRow()
				.quantity(0)
				.descriptions(List.of(format(DESCRIPTIONS_ROW_1, FLOW_INSTANCE_ID))),
			new InvoiceRow()
				.quantity(0)
				.descriptions(List.of(format(DESCRIPTIONS_ROW_2, REFERENCE_NAME, REFERENCE_CODE))),
			new InvoiceRow()
				.quantity(0)
				.descriptions(List.of(format(DESCRIPTIONS_ROW_3, FIRST_NAME, LAST_NAME, USERNAME))),
			new InvoiceRow()
				.quantity(1)
				.totalAmount(TOTAL_AMOUNT_WITHOUT_PHOTO)
				.descriptions(List.of(format(DESCRIPTIONS_ROW_4_WITHOUT_PHOTO, FIRST_NAME, LAST_NAME, USERNAME)))
				.accountInformation(new AccountInformation()
					.activity(ACTIVITY)
					.costCenter(COST_CENTER)
					.department(DEPARTMENT)
					.subaccount(SUBACCOUNT)));
	}

	@Test
	void toBillingRecordWhenAccessCardEntityIsNull() {

		// Call
		final var exception = assertThrows(ThrowableProblem.class, () -> AccessCardMapper.toBillingRecord(null));

		// Verification
		assertThat(exception.getMessage()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase() + ": " + ERROR_MESSAGE_OBJECTS_MISSING);
	}

	@Test
	void toAccessCardEntity() {

		// Setup
		final var skReferensNummerList = List.of(createSKReferensNummer());
		final var flowInstance = createGetInstanceFlowInstance();

		final var result = AccessCardMapper.toAccessCardEntity(flowInstance, skReferensNummerList);

		// Verification
		assertThat(result).isNotNull();
		assertThat(result.getFirstName()).isEqualTo(GET_INSTANCE_FIRSTNAME);
		assertThat(result.getLastName()).isEqualTo(GET_INSTANCE_LASTNAME);
		assertThat(result.getUsername()).isEqualTo(GET_INSTANCE_USERNAME);
		assertThat(result.getFlowInstanceId()).isEqualTo(GET_INSTANCE_FLOW_INSTANCE_ID);
		assertThat(result.hasPhoto()).isTrue();
		assertThat(result.getPosted()).isEqualTo(POSTED);
		assertThat(result.getReferenceCode()).isEqualTo(GET_INSTANCE_EXPECTED_REFERENCE_CODE);
		assertThat(result.getReferenceCode()).isEqualTo(GET_INSTANCE_EXPECTED_REFERENCE_CODE);
		assertThat(result.getReferenceCodeId()).isEqualTo(REFKOD_ID.toString());
		assertThat(result.getReferenceName()).isEqualTo(ANV_NAMN);
	}

	@Test
	void toAccessCardEntityWhenCaseTypeIsNotSupported() {

		// Setup
		final var skReferensNummerList = List.of(createSKReferensNummer());
		final var flowInstance = createGetInstanceFlowInstance();
		flowInstance.getValues().caseType(new FlowInstanceValue().value("whatever"));

		final var result = AccessCardMapper.toAccessCardEntity(flowInstance, skReferensNummerList);

		// Verification
		assertThat(result).isNull();
	}

	@Test
	void toFlowInstanceId() {

		// Setup
		final var flowInstance = createGetInstancesFlowInstance();

		final var result = AccessCardMapper.toFlowInstanceId(flowInstance);

		// Verification
		assertThat(result).isEqualTo(parseInt(GET_INSTANCES_FLOW_INSTANCE_ID));
	}

	private static AccessCardEntity createAccessCardEntity(final boolean withPhoto) {
		return new AccessCardEntity()
			.withCreated(now())
			.withFirstName(FIRST_NAME)
			.withLastName(LAST_NAME)
			.withFlowInstanceId(FLOW_INSTANCE_ID)
			.withId(ID)
			.withModified(now())
			.withPhoto(withPhoto)
			.withPosted(POSTED)
			.withReferenceCode(REFERENCE_CODE)
			.withReferenceCodeId(REFERENCE_CODE_ID)
			.withReferenceName(REFERENCE_NAME)
			.withStatus(UNPROCESSED)
			.withUsername(USERNAME);
	}

	private static SKReferensNummer createSKReferensNummer() {
		return new SKReferensNummer()
			.ANV_NAMN(ANV_NAMN)
			.BESKRIVNING(BESKRIVNING)
			.REFKOD(REFKOD)
			.REFKOD_ID(REFKOD_ID);
	}

	private static generated.se.sundsvall.oep.getinstances.FlowInstance createGetInstancesFlowInstance() {
		return new generated.se.sundsvall.oep.getinstances.FlowInstance()
			.flowInstanceID(parseInt(GET_INSTANCES_FLOW_INSTANCE_ID));
	}

	private static generated.se.sundsvall.oep.getinstance.FlowInstance createGetInstanceFlowInstance() {
		return new generated.se.sundsvall.oep.getinstance.FlowInstance()
			.header(new FlowInstanceHeader()
				.flowInstanceID(parseInt(GET_INSTANCE_FLOW_INSTANCE_ID))
				.posted(GET_INSTANCE_POSTED))
			.values(new FlowInstanceValues()
				.internalContactData(new FlowInstanceValuesInternalContactData()
					.firstname(GET_INSTANCE_FIRSTNAME)
					.lastname(GET_INSTANCE_LASTNAME)
					.username(GET_INSTANCE_USERNAME))
				.accessCardPhoto(new FlowInstanceValuesAccessCardPhoto())
				.referenceCode(new FlowInstanceValue()
					.value(GET_INSTANCE_REFERENCE_CODE))
				.caseType(new FlowInstanceValue().value(CASE_TYPE_TO_PROCESS)));
	}
}
