package se.sundsvall.billingdatapolling.integration.billingpreprocessor.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.billingpreprocessor")
public record BillingPreProcessorProperties(int connectTimeout, int readTimeout) {
}
