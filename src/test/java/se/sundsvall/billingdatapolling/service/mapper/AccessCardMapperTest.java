package se.sundsvall.billingdatapolling.service.mapper;

import static generated.se.sundsvall.billingpreprocessor.Status.APPROVED;
import static java.lang.String.format;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.billingdatapolling.integration.db.model.enums.Status.UNPROCESSED;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.ACTIVITY;
import static se.sundsvall.billingdatapolling.service.mapper.AccessCardMapper.APPROVED_BY;
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

import java.util.List;

import org.junit.jupiter.api.Test;
import org.zalando.problem.ThrowableProblem;

import generated.se.sundsvall.billingpreprocessor.AccountInformation;
import generated.se.sundsvall.billingpreprocessor.InvoiceRow;
import generated.se.sundsvall.smex.skreferensnummer.SKReferensNummer;
import se.sundsvall.billingdatapolling.integration.db.model.AccessCardEntity;

class AccessCardMapperTest {

	// AccessCardEntity values.
	private static final Long ID = 666L;
	private static final String FIRST_NAME = "John";
	private static final String LAST_NAME = "Doe";
	private static final String FLOW_INSTANCE_ID = "flowInstanceId";
	private static final String REFERENCE_CODE = "referenceCode";
	private static final String USERNAME = "username";

	// SKReferensNummer values.
	private static final String ANV_NAMN = "anv_namn";
	private static final String BESKRIVNING = "beskrivning";
	private static final String REFKOD = "REFKOD";
	private static final Long REFKOD_ID = 1337L;

	@Test
	void toBillingRecordWithPhoto() {

		// Setup
		final var withPhoto = true;
		final var accessCardEntity = createAccessCardEntity(withPhoto);
		final var skReferensNummer = createSKReferensNummer();

		// Call
		final var result = AccessCardMapper.toBillingRecord(accessCardEntity, skReferensNummer);

		// Verification
		assertThat(result).isNotNull();
		assertThat(result.getApprovedBy()).isEqualTo(APPROVED_BY);
		assertThat(result.getCategory()).isEqualTo(CATEGORY);
		assertThat(result.getType()).isEqualTo(TYPE);
		assertThat(result.getStatus()).isEqualTo(APPROVED);
		assertThat(result.getInvoice()).isNotNull();
		assertThat(result.getInvoice().getCustomerId()).isEqualTo(CUSTOMER_ID);
		assertThat(result.getInvoice().getOurReference()).isEqualTo(ANV_NAMN);
		assertThat(result.getInvoice().getReferenceId()).isEqualTo(REFKOD_ID.toString());
		assertThat(result.getInvoice().getTotalAmount()).isEqualTo(TOTAL_AMOUNT_WITH_PHOTO);
		assertThat(result.getInvoice().getInvoiceRows()).hasSize(4).containsExactly(
			new InvoiceRow()
				.quantity(0)
				.descriptions(List.of(format(DESCRIPTIONS_ROW_1, FLOW_INSTANCE_ID))),
			new InvoiceRow()
				.quantity(0)
				.descriptions(List.of(format(DESCRIPTIONS_ROW_2, ANV_NAMN, REFKOD))),
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
		final var skReferensNummer = createSKReferensNummer();

		// Call
		final var result = AccessCardMapper.toBillingRecord(accessCardEntity, skReferensNummer);

		// Verification
		assertThat(result).isNotNull();
		assertThat(result.getApprovedBy()).isEqualTo(APPROVED_BY);
		assertThat(result.getCategory()).isEqualTo(CATEGORY);
		assertThat(result.getType()).isEqualTo(TYPE);
		assertThat(result.getStatus()).isEqualTo(APPROVED);
		assertThat(result.getInvoice()).isNotNull();
		assertThat(result.getInvoice().getCustomerId()).isEqualTo(CUSTOMER_ID);
		assertThat(result.getInvoice().getOurReference()).isEqualTo(ANV_NAMN);
		assertThat(result.getInvoice().getReferenceId()).isEqualTo(REFKOD_ID.toString());
		assertThat(result.getInvoice().getTotalAmount()).isEqualTo(TOTAL_AMOUNT_WITHOUT_PHOTO);
		assertThat(result.getInvoice().getInvoiceRows()).hasSize(4).containsExactly(
			new InvoiceRow()
				.quantity(0)
				.descriptions(List.of(format(DESCRIPTIONS_ROW_1, FLOW_INSTANCE_ID))),
			new InvoiceRow()
				.quantity(0)
				.descriptions(List.of(format(DESCRIPTIONS_ROW_2, ANV_NAMN, REFKOD))),
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

		// Setup
		final var skReferensNummer = createSKReferensNummer();

		// Call
		final var exception = assertThrows(ThrowableProblem.class, () -> AccessCardMapper.toBillingRecord(null, skReferensNummer));

		assertThat(exception.getMessage()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase() + ": " + ERROR_MESSAGE_OBJECTS_MISSING);
	}

	@Test
	void toBillingRecordWhenSKReferensNummerIsNull() {

		// Setup
		final var withPhoto = false;
		final var accessCardEntity = createAccessCardEntity(withPhoto);

		// Call
		final var exception = assertThrows(ThrowableProblem.class, () -> AccessCardMapper.toBillingRecord(accessCardEntity, null));

		assertThat(exception.getMessage()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase() + ": " + ERROR_MESSAGE_OBJECTS_MISSING);
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
			.withReferenceCode(REFERENCE_CODE)
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
}
