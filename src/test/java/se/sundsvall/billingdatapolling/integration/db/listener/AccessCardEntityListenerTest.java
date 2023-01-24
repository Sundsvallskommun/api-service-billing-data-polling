package se.sundsvall.billingdatapolling.integration.db.listener;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static se.sundsvall.billingdatapolling.integration.db.model.enums.Status.PROCESSED;
import static se.sundsvall.billingdatapolling.integration.db.model.enums.Status.UNPROCESSED;

import org.junit.jupiter.api.Test;

import se.sundsvall.billingdatapolling.integration.db.model.AccessCardEntity;

class AccessCardEntityListenerTest {

	@Test
	void prePerist() {

		// Setup
		final var listener = new AccessCardEntityListener();
		final var entity = new AccessCardEntity();

		// Call
		listener.prePersist(entity);

		// Assertions
		assertThat(entity).hasAllNullFieldsOrPropertiesExcept("created", "status");
		assertThat(entity.getCreated()).isCloseTo(now(), within(2, SECONDS));
		assertThat(entity.getStatus()).isEqualTo(UNPROCESSED);
	}

	@Test
	void preUpdate() {

		// Setup
		final var listener = new AccessCardEntityListener();
		final var entity = new AccessCardEntity();

		// Call
		listener.preUpdate(entity);

		// Assertions
		assertThat(entity).hasAllNullFieldsOrPropertiesExcept("modified");
		assertThat(entity.getModified()).isCloseTo(now(), within(2, SECONDS));
	}

	@Test
	void preUpdateWhenStatusIsSetToProcessed() {

		// Setup
		final var listener = new AccessCardEntityListener();
		final var entity = new AccessCardEntity();

		// Update
		entity.setStatus(PROCESSED);

		// Call
		listener.preUpdate(entity);

		// Assertions
		assertThat(entity).hasAllNullFieldsOrPropertiesExcept("modified", "status", "processed");
		assertThat(entity.getStatus()).isEqualTo(PROCESSED);
		assertThat(entity.getModified()).isCloseTo(now(), within(2, SECONDS));
		assertThat(entity.getProcessed()).isCloseTo(now(), within(2, SECONDS));
	}
}
