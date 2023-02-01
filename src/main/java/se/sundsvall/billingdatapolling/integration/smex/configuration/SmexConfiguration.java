package se.sundsvall.billingdatapolling.integration.smex.configuration;

import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;

@Import(FeignConfiguration.class)
public class SmexConfiguration {

	public static final String CLIENT_ID = "smex";

	@Bean
	FeignBuilderCustomizer feignBuilderCustomizer( SmexProperties properties) {
		return FeignMultiCustomizer.create()
			.withRequestTimeoutsInSeconds(properties.connectTimeout(), properties.readTimeout())
			.composeCustomizersToOne();
	}
}
