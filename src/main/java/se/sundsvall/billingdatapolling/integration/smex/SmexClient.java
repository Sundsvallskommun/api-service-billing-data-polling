package se.sundsvall.billingdatapolling.integration.smex;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.billingdatapolling.integration.smex.configuration.SmexConfiguration.CLIENT_ID;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import generated.se.sundsvall.smex.skreferensnummer.SKReferensNummer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.billingdatapolling.integration.smex.configuration.SmexConfiguration;

@FeignClient(name = CLIENT_ID, url = "${integration.smex.url}", configuration = SmexConfiguration.class)
@CircuitBreaker(name = CLIENT_ID)
public interface SmexClient {

	/**
	 * Get SK_REFERENSNUMMER mapping
	 *
	 * @return a list of SKReferensNummer
	 */
	@Cacheable("skreferensnummer")
	@GetMapping(path = "/SK_REFERENSNUMMER", produces = { APPLICATION_JSON_VALUE })
	List<SKReferensNummer> getSKReferensNummer();
}
