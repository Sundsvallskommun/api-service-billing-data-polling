package se.sundsvall.billingdatapolling.integration.oep.configuration;

import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import feign.auth.BasicAuthRequestInterceptor;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;

@Import(FeignConfiguration.class)
public class OepGetInstanceConfiguration {

	public static final String CLIENT_ID = "oepgetinstance";

	@Bean
	FeignBuilderCustomizer feignBuilderCustomizer( OepGetInstanceProperties properties) {
		return FeignMultiCustomizer.create()
			.withRequestInterceptor(new BasicAuthRequestInterceptor(properties.username(), properties.password()))
			.withRequestTimeoutsInSeconds(properties.connectTimeout(), properties.readTimeout())
			.composeCustomizersToOne();
	}
}
