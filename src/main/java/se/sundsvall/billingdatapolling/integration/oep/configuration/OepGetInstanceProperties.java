package se.sundsvall.billingdatapolling.integration.oep.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.oepgetinstance")
public record OepGetInstanceProperties(int connectTimeout, int readTimeout, String username, String password) {
}
