package se.sundsvall.billingdatapolling.service.mapper;

import static generated.se.sundsvall.billingpreprocessor.Status.APPROVED;
import static generated.se.sundsvall.billingpreprocessor.Type.INTERNAL;
import static java.lang.String.format;
import static org.apache.commons.lang3.ObjectUtils.anyNull;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.zalando.problem.Problem;

import generated.se.sundsvall.billingpreprocessor.AccountInformation;
import generated.se.sundsvall.billingpreprocessor.BillingRecord;
import generated.se.sundsvall.billingpreprocessor.Invoice;
import generated.se.sundsvall.billingpreprocessor.InvoiceRow;
import generated.se.sundsvall.billingpreprocessor.Type;
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

	// Error message
	static final String ERROR_MESSAGE_OBJECTS_MISSING = "Mapping to BillingRecord not possible. One or more of the input objects was null.";

	private AccessCardMapper() {}

	public static BillingRecord toBillingRecord( AccessCardEntity accessCardEntity,  SKReferensNummer skReferensNummer) {

		if (anyNull(accessCardEntity, skReferensNummer)) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, ERROR_MESSAGE_OBJECTS_MISSING);
		}

		return new BillingRecord()
			.category(CATEGORY)
			.type(TYPE)
			.status(APPROVED)
			.approvedBy(APPROVED_BY)
			.invoice(new Invoice()
				.customerId(CUSTOMER_ID)
				.ourReference(toOurReference(skReferensNummer))
				.referenceId(toReferenceId(skReferensNummer))
				.totalAmount(toTotalAmount(accessCardEntity))
				.invoiceRows(toInvoiceRows(accessCardEntity, skReferensNummer)));
	}

	private static List<InvoiceRow> toInvoiceRows( AccessCardEntity accessCardEntity,  SKReferensNummer skReferensNummer) {
		return List.of(
			new InvoiceRow()
				.addDescriptionsItem(format(DESCRIPTIONS_ROW_1, accessCardEntity.getFlowInstanceId()))
				.quantity(0),
			new InvoiceRow()
				.addDescriptionsItem(format(DESCRIPTIONS_ROW_2, skReferensNummer.getANVNAMN(), skReferensNummer.getREFKOD()))
				.quantity(0),
			new InvoiceRow()
				.addDescriptionsItem(format(DESCRIPTIONS_ROW_3, accessCardEntity.getFirstName(), accessCardEntity.getLastName(), accessCardEntity.getUsername()))
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

	private static float toTotalAmount( AccessCardEntity accessCardEntity) {
		if (accessCardEntity.hasPhoto()) {
			return TOTAL_AMOUNT_WITH_PHOTO;
		}

		return TOTAL_AMOUNT_WITHOUT_PHOTO;
	}

	private static String toReferenceId( SKReferensNummer skReferensNummer) {
		return Optional.ofNullable(skReferensNummer)
			.map(SKReferensNummer::getREFKODID)
			.map(Objects::toString)
			.orElse(null);
	}

	private static String toOurReference( SKReferensNummer skReferensNummer) {
		return Optional.ofNullable(skReferensNummer)
			.map(SKReferensNummer::getANVNAMN)
			.map(Objects::toString)
			.orElse(null);
	}
}
