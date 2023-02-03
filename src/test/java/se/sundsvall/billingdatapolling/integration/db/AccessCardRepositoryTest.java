package se.sundsvall.billingdatapolling.integration.db;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.groups.Tuple.tuple;
import static se.sundsvall.billingdatapolling.integration.db.model.enums.Status.FAILED;
import static se.sundsvall.billingdatapolling.integration.db.model.enums.Status.PROCESSED;
import static se.sundsvall.billingdatapolling.integration.db.model.enums.Status.UNPROCESSED;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.billingdatapolling.integration.db.model.AccessCardEntity;

/**
 * AccessCardRepository tests
 *
 * @see /src/test/resources/db/testdata-junit.sql for data setup.
 */
@SpringBootTest
@ActiveProfiles("junit")
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-junit.sql"
})
class AccessCardRepositoryTest {

	private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";
	private static final String FLOW_INSTANCE_ID = "flowInstanceId";
	private static final String REFERENCE_CODE = "referenceCode";
	private static final String USERNAME = "username";
	private static final Boolean PHOTO = true;

	@Autowired
	private AccessCardRepository repository;

	@Test
	void create() {

		final var accessCard = createAccessCardEntity();

		final var result = repository.save(accessCard);

		// Verification
		assertThat(result).isNotNull();
		assertThat(result.getId()).isPositive();
		assertThat(result.getFirstName()).isEqualTo(FIRST_NAME);
		assertThat(result.getLastName()).isEqualTo(LAST_NAME);
		assertThat(result.getFlowInstanceId()).isEqualTo(FLOW_INSTANCE_ID);
		assertThat(result.getReferenceCode()).isEqualTo(REFERENCE_CODE);
		assertThat(result.getUsername()).isEqualTo(USERNAME);
		assertThat(result.getStatus()).isEqualTo(UNPROCESSED);
		assertThat(result.getPhoto()).isEqualTo(PHOTO);
		assertThat(result.getCreated()).isCloseTo(now(), within(2, SECONDS));
		assertThat(result.getModified()).isNull();
		assertThat(result.getProcessed()).isNull();
	}

	@Test
	void findById() {

		// Setup
		final var id = 1L;
		final var flowInstanceId = "111111";

		final var result = repository.findById(id).orElseThrow();

		// Verification
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getFirstName()).isEqualTo("John");
		assertThat(result.getLastName()).isEqualTo("Doe");
		assertThat(result.getFlowInstanceId()).isEqualTo(flowInstanceId);
	}

	@Test
	void findByIdNotFound() {
		assertThat(repository.findById(123456789L)).isNotPresent();
	}

	@Test
	void findByFlowInstanceId() {

		// Setup
		final var id = 2L;
		final var flowInstanceId = "222222";

		final var result = repository.findByFlowInstanceId(flowInstanceId).orElseThrow();

		// Verification
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getFirstName()).isEqualTo("Jane");
		assertThat(result.getLastName()).isEqualTo("Smith");
		assertThat(result.getFlowInstanceId()).isEqualTo(flowInstanceId);
	}

	@Test
	void findByFlowInstanceIdNotFound() {
		assertThat(repository.findByFlowInstanceId("non-existing-flowInstanceId")).isNotPresent();
	}

	@Test
	void update() {

		// Setup
		final var id = 3L;

		// Fetch existing entity.
		final var accessCard = repository.findById(id).orElseThrow();
		assertThat(accessCard.getStatus()).isEqualTo(UNPROCESSED);
		assertThat(accessCard.getProcessed()).isNull();
		assertThat(accessCard.getModified()).isNull();

		// Update entity.
		accessCard.setStatus(PROCESSED);
		repository.save(accessCard);

		// Verification
		final var result = repository.findById(id).orElseThrow();
		assertThat(result.getStatus()).isEqualTo(PROCESSED);
		assertThat(result.getProcessed()).isCloseTo(now(), within(2, SECONDS));
		assertThat(result.getModified()).isCloseTo(now(), within(2, SECONDS));
	}

	@Test
	void deleteById() {

		// Setup
		final var id = 4L;

		assertThat(repository.findById(id)).isPresent();

		repository.deleteById(id);

		// Verification
		assertThat(repository.findById(id)).isNotPresent();
	}

	@Test
	void findByStatusIn() {

		// Call
		final var result = repository.findByStatusIn(List.of(FAILED));

		// Verification
		assertThat(result).hasSize(2);
		assertThat(result)
			.extracting(AccessCardEntity::getId, AccessCardEntity::getFlowInstanceId, AccessCardEntity::getStatus)
			.containsExactlyInAnyOrder(
				tuple(4L, "444444", FAILED),
				tuple(5L, "555555", FAILED));
	}

	@Test
	void findFirstByOrderByPostedDesc() {

		// Call
		final var result = repository.findFirstByOrderByPostedDesc().orElseThrow();

		// Verification
		assertThat(result.getId()).isEqualTo(6L);
	}

	private static AccessCardEntity createAccessCardEntity() {
		return new AccessCardEntity()
			.withFirstName(FIRST_NAME)
			.withLastName(LAST_NAME)
			.withFlowInstanceId(FLOW_INSTANCE_ID)
			.withReferenceCode(REFERENCE_CODE)
			.withUsername(USERNAME)
			.withPhoto(PHOTO);
	}
}
