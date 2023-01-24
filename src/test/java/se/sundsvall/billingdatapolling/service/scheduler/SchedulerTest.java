package se.sundsvall.billingdatapolling.service.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.billingdatapolling.service.scheduler.configuration.SchedulerProperties;

@ExtendWith(MockitoExtension.class)
class SchedulerTest {

	@Mock
	private SchedulerProperties propertiesMock;

	@InjectMocks
	private Scheduler scheduler;

	@Test
	void execute() {

		final var exception = assertThrows(NotImplementedException.class, () -> scheduler.execute());

		assertThat(exception.getMessage()).isEqualTo("I'm not there yet!");
	}

	@Test
	void getSchedulerInformation() {

		when(propertiesMock.expression()).thenReturn("1 3 3 7 * *");
		when(propertiesMock.enabled()).thenReturn(true);

		final var schedulerInformation = scheduler.getSchedulerInformation();

		assertThat(schedulerInformation).isNotNull();
		assertThat(schedulerInformation.getDescription()).isEqualTo("At 03:03:01, on day 7 of the month");
		assertThat(schedulerInformation.getExpression()).isEqualTo("1 3 3 7 * *");
		assertThat(schedulerInformation.isEnabled()).isTrue();

		verify(propertiesMock).enabled();
		verify(propertiesMock, times(2)).expression();
	}
}
