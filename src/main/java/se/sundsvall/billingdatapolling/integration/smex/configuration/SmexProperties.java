package se.sundsvall.billingdatapolling.integration.smex.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.smex")
public record SmexProperties(int connectTimeout, int readTimeout) {
}
