package se.sundsvall.billingdatapolling.integration.oep;

import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static se.sundsvall.billingdatapolling.integration.oep.configuration.OepGetInstanceConfiguration.CLIENT_ID;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.billingdatapolling.integration.oep.configuration.OepGetInstanceConfiguration;

@FeignClient(name = CLIENT_ID, url = "${integration.oepgetinstance.url}", configuration = OepGetInstanceConfiguration.class)
@CircuitBreaker(name = CLIENT_ID)
public interface OepGetInstanceClient {

	/**
	 * Fetch the list of FlowInstance-objects from OEP. Uses API defined in oep-getinstances.yaml
	 *
	 * @param fromDate the filter fromDate.
	 * @param toDate   the filter toDate.
	 * @return a List of FlowInstance that matches the provided parameters.
	 */
	@GetMapping(path = "/getinstances/family/${integration.oepgetinstance.family-id}", produces = { APPLICATION_XML_VALUE })
	List<generated.se.sundsvall.oep.getinstances.FlowInstance> getInstancesFilteredByDate(@RequestParam("fromDate") LocalDate fromDate, @RequestParam("toDate") LocalDate toDate);

	/**
	 * Fetch the full FlowInstance-object from OEP. Uses API defined in oep-getinstance.yaml
	 *
	 * @param flowInstanceId the flowInstanceId of the flowInstance to get.
	 * @return a FlowInstance that matches the provided parameter.
	 */
	@GetMapping(path = "/getinstance/{flowInstanceId}/xml", produces = { APPLICATION_XML_VALUE })
	generated.se.sundsvall.oep.getinstance.FlowInstance getInstanceByFlowInstanceId(@PathVariable("flowInstanceId") Integer flowInstanceId);
}
