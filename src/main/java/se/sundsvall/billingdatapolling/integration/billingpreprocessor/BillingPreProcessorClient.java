package se.sundsvall.billingdatapolling.integration.billingpreprocessor;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static se.sundsvall.billingdatapolling.integration.billingpreprocessor.configuration.BillingPreProcessorConfiguration.CLIENT_ID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import generated.se.sundsvall.billingpreprocessor.BillingRecord;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.billingdatapolling.integration.billingpreprocessor.configuration.BillingPreProcessorConfiguration;

@FeignClient(name = CLIENT_ID, url = "${integration.billingpreprocessor.url}", configuration = BillingPreProcessorConfiguration.class)
@CircuitBreaker(name = CLIENT_ID)
public interface BillingPreProcessorClient {

	@PostMapping(path = "billingrecords", consumes = APPLICATION_JSON_VALUE, produces = { APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	void createBillingRecord(BillingRecord billingRecord);
}
