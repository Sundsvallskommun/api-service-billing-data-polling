package se.sundsvall.billingdatapolling.service.scheduler.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("scheduler.cron")
public record SchedulerProperties(boolean enabled, String expression) {
}
