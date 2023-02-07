package se.sundsvall.billingdatapolling.integration.messaging;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.billingdatapolling.integration.messaging.configuration.MessagingConfiguration.CLIENT_ID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import generated.se.sundsvall.messaging.EmailRequest;
import generated.se.sundsvall.messaging.MessageResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.billingdatapolling.integration.messaging.configuration.MessagingConfiguration;

@FeignClient(name = CLIENT_ID, url = "${integration.messaging.url}", configuration = MessagingConfiguration.class)
@CircuitBreaker(name = CLIENT_ID)
public interface MessagingClient {

	/**
	 * Send a single e-mail
	 *
	 * @param emailRequest containing email information
	 * @return response containing id for sent message
	 */
	@PostMapping(path = "/email", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	MessageResponse sendEmail(@RequestBody EmailRequest emailRequest);
}
