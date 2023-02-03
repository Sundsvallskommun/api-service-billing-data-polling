package se.sundsvall.billingdatapolling.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import se.sundsvall.billingdatapolling.Application;
import se.sundsvall.billingdatapolling.api.model.SchedulerInformation;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class InformationResourceTest {

	private static final String PATH = "/information/schedulers";

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getSchedulerInformation() {

		// Call
		final var response = webTestClient.get().uri(builder -> builder.path(PATH).build())
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(SchedulerInformation.class)
			.returnResult()
			.getResponseBody();

		// Verification
		assertThat(response)
			.hasSize(1)
			.extracting(
				SchedulerInformation::getDescription, SchedulerInformation::getExpression, SchedulerInformation::getName)
			.containsExactly(
				tuple("At 00:10, every day", "0 10 0 * * *", "AccessCardScheduleService"));
	}
}
