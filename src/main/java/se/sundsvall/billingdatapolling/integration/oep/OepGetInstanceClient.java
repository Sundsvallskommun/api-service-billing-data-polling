package se.sundsvall.billingdatapolling.integration.oep;

import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static se.sundsvall.billingdatapolling.integration.oep.configuration.OepGetInstanceConfiguration.CLIENT_ID;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import generated.se.sundsvall.oep.getinstance.FlowInstance;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.billingdatapolling.integration.oep.configuration.OepGetInstanceConfiguration;

@FeignClient(name = CLIENT_ID, url = "${integration.oepgetinstance.url}", configuration = OepGetInstanceConfiguration.class)
@CircuitBreaker(name = CLIENT_ID)
public interface OepGetInstanceClient {

	@GetMapping(path = "/getinstances/family/${integration.oepgetinstance.family-id}", produces = { APPLICATION_XML_VALUE })
	List<FlowInstance> getInstancesFilteredByDate(@RequestParam("fromDate") LocalDate fromDate, @RequestParam("toDate") LocalDate toDate);
}
