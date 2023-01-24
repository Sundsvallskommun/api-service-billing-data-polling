package se.sundsvall.billingdatapolling.service.scheduler.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = { SchedulerConfiguration.class })
class SchedulerConfigurationTest {

	@Nested
	@TestPropertySource(properties = {
		"scheduler.cron.enabled=true"
	})
	class SchedulerConfigurationWhenEnabledTest {

		@Autowired(required = false)
		private SchedulerConfiguration schedulerConfiguration;

		@Test
		void testScheduler() {
			assertThat(schedulerConfiguration).isNotNull();
		}
	}

	@Nested
	@TestPropertySource(properties = {
		"scheduler.cron.enabled=false"
	})
	class SchedulerConfigurationWhenDisabledTest {

		@Autowired(required = false)
		private SchedulerConfiguration schedulerConfiguration;

		@Test
		void testScheduler() {
			assertThat(schedulerConfiguration).isNull();
		}
	}

	@Nested
	class SchedulerConfigurationWhenPropertyMissingTest {

		@Autowired(required = false)
		private SchedulerConfiguration schedulerConfiguration;

		@Test
		void testScheduler() {
			assertThat(schedulerConfiguration).isNotNull();
		}
	}
}
