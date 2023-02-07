package se.sundsvall.billingdatapolling.service.messaging;

import static com.nimbusds.oauth2.sdk.util.CollectionUtils.isEmpty;
import static java.lang.String.format;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import generated.se.sundsvall.messaging.Email;
import generated.se.sundsvall.messaging.EmailRequest;
import se.sundsvall.billingdatapolling.integration.db.model.AccessCardEntity;
import se.sundsvall.billingdatapolling.integration.messaging.MessagingClient;

@Service
public class MessagingService {

	private static final String ACCESS_CARD_FAILURE_SUBJECT = "Failure in %s (%s)";
	private static final String ACCESS_CARD_FAILURE_BODY_PREFIX = "%s failed record(s) exist in %s-database! %n%nErrors:%n";
	private static final String ACCESS_CARD_FAILURE_BODY_ROW = "ID:%s | %s %n";

	@Value("${spring.application.name:}")
	private String applicationName;

	@Value("${spring.profiles.active:}")
	private String applicationEnvironment;

	@Value("${information.mail.recipient:}")
	private String recipientAddress;

	@Value("${information.mail.sender:}")
	private String senderAddress;

	@Autowired
	private MessagingClient messagingClient;

	/**
	 * Sends failure message to configured recipient.
	 * ID and statusMessage for all provided AccessCardEntities will be appended to mail message.
	 *
	 * @param failedAccessCards a List of failed accessCards.
	 */
	public void sendFailureMessage(final List<AccessCardEntity> failedAccessCards) {
		if (isEmpty(failedAccessCards)) {
			return;
		}

		final var subject = format(ACCESS_CARD_FAILURE_SUBJECT, applicationName, applicationEnvironment);
		final var message = new StringBuilder().append(format(ACCESS_CARD_FAILURE_BODY_PREFIX, failedAccessCards.size(), applicationEnvironment));

		failedAccessCards.forEach(failedRecord -> message
			.append(format(ACCESS_CARD_FAILURE_BODY_ROW, failedRecord.getId(), failedRecord.getStatusMessage())));

		sendMail(subject, message.toString());
	}

	private void sendMail(final String subject, final String message) {
		messagingClient.sendEmail(createEmailMessage(subject, message));
	}

	private EmailRequest createEmailMessage(final String subject, final String message) {
		return new EmailRequest()
			.sender(new Email()
				.name(applicationName)
				.address(senderAddress))
			.emailAddress(recipientAddress)
			.subject(subject)
			.message(message);
	}
}
