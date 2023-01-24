package se.sundsvall.billingdatapolling.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

import se.sundsvall.billingdatapolling.Application;
import se.sundsvall.billingdatapolling.api.model.PollingRequest;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class PollingAccessCardsResourceFailureTest {

	private static final String PATH = "/pollings/access-cards";

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void createPollingWithNullBody() {

		// Call
		final var response = webTestClient.post().uri(PATH)
			.contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Bad Request");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getDetail()).isEqualTo("""
			Required request body is missing: public org.springframework.http.ResponseEntity<java.lang.Void> \
			se.sundsvall.billingdatapolling.api.PollingResource.pollAccessCards(se.sundsvall.billingdatapolling.api.model.PollingRequest)""");

		// TODO: Add verification of no interaction
		// Verification
		// verifyNoInteractions(serviceMock);
	}

	@Test
	void createPollingWithEmptyBody() {

		// Parameters
		final var request = PollingRequest.create();

		// Call
		final var response = webTestClient.post().uri(PATH)
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactlyInAnyOrder(
			tuple("fromDate", "must not be null"),
			tuple("toDate", "must not be null"));

		// TODO: Add verification of no interaction
		// Verification
		// verifyNoInteractions(serviceMock);
	}

	@Test
	void createPollingWithFutureFromDateAndToDate() {

		// Parameters
		final var request = PollingRequest.create()
			.withFromDate(LocalDate.now().plusDays(1))
			.withToDate(LocalDate.now().plusDays(2));

		// Call
		final var response = webTestClient.post().uri(PATH)
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactlyInAnyOrder(
			tuple("fromDate", "must be a date in the past or in the present"),
			tuple("toDate", "must be a date in the past or in the present"));

		// TODO: Add verification of no interaction
		// Verification
		// verifyNoInteractions(serviceMock);
	}
}
