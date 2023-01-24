package se.sundsvall.billingdatapolling.api;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import se.sundsvall.billingdatapolling.Application;
import se.sundsvall.billingdatapolling.api.model.PollingRequest;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class PollingAccessCardsResourceTest {

	private static final String PATH = "/pollings/access-cards";

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void createPolling() {

		// Parameters
		final var request = PollingRequest.create()
			.withFromDate(LocalDate.now().minusDays(1))
			.withToDate(LocalDate.now());

		// Call
		webTestClient.post().uri(PATH)
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isNoContent();

		// TODO: Add verification of interaction
		// Verification
		// verifyNoInteractions(serviceMock);
	}
}
