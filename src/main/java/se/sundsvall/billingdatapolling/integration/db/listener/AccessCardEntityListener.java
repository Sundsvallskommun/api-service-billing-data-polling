package se.sundsvall.billingdatapolling.integration.db.listener;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Objects.isNull;
import static se.sundsvall.billingdatapolling.integration.db.model.enums.Status.PROCESSED;
import static se.sundsvall.billingdatapolling.integration.db.model.enums.Status.UNPROCESSED;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import se.sundsvall.billingdatapolling.integration.db.model.AccessCardEntity;

public class AccessCardEntityListener {

	@PrePersist
	void prePersist(final AccessCardEntity entity) {
		entity.setCreated(now().truncatedTo(MILLIS));
		entity.setStatus(UNPROCESSED);
	}

	@PreUpdate
	void preUpdate(final AccessCardEntity entity) {
		final var now = now().truncatedTo(MILLIS);

		entity.setModified(now);

		if (PROCESSED.equals(entity.getStatus()) && isNull(entity.getProcessed())) {
			entity.setProcessed(now);
		}
	}
}
