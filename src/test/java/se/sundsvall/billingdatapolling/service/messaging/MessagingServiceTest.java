package se.sundsvall.billingdatapolling.service.messaging;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import generated.se.sundsvall.messaging.EmailRequest;
import se.sundsvall.billingdatapolling.integration.db.model.AccessCardEntity;
import se.sundsvall.billingdatapolling.integration.messaging.MessagingClient;

@ExtendWith(MockitoExtension.class)
class MessagingServiceTest {

	@Mock
	private MessagingClient messagingClientMock;

	@InjectMocks
	private MessagingService messgingService;

	@Captor
	private ArgumentCaptor<EmailRequest> emailRequestCaptor;

	@BeforeEach
	void setup() {
		setField(messgingService, "applicationName", "MyApplication");
		setField(messgingService, "applicationEnvironment", "test");
		setField(messgingService, "recipientAddress", "recipient@host.com");
		setField(messgingService, "senderAddress", "sender@host.com");
	}

	@Test
	void sendFailureMessage() {

		// Setup
		final var failedAccessCards = List.of(
			AccessCardEntity.create()
				.withId(1L)
				.withStatusMessage("statusMessage 1"),
			AccessCardEntity.create()
				.withId(2L)
				.withStatusMessage("statusMessage 2"),
			AccessCardEntity.create()
				.withId(3L)
				.withStatusMessage("statusMessage 3"));

		// Call
		messgingService.sendFailureMessage(failedAccessCards);

		// Verification
		verify(messagingClientMock).sendEmail(emailRequestCaptor.capture());

		final var capturedEmailRequest = emailRequestCaptor.getValue();
		assertThat(capturedEmailRequest).isNotNull();
		assertThat(capturedEmailRequest.getEmailAddress()).isEqualTo("recipient@host.com");
		assertThat(capturedEmailRequest.getSender().getName()).isEqualTo("MyApplication");
		assertThat(capturedEmailRequest.getSender().getAddress()).isEqualTo("sender@host.com");
		assertThat(capturedEmailRequest.getSubject()).isEqualTo("Failure in MyApplication (test)");
		assertThat(capturedEmailRequest.getMessage()).isEqualToIgnoringWhitespace("""
				3 failed record(s) exist in test-database!

				Errors:
				ID:1 | statusMessage 1
				ID:2 | statusMessage 2
				ID:3 | statusMessage 3
			""");
	}

	@Test
	void sendFailureMessageWhenEmptyList() {

		// Call
		messgingService.sendFailureMessage(emptyList());

		// Verification
		verifyNoInteractions(messagingClientMock);
	}
}
