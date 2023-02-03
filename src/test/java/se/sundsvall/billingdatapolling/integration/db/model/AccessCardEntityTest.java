package se.sundsvall.billingdatapolling.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.time.OffsetDateTime.now;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static se.sundsvall.billingdatapolling.integration.db.model.enums.Status.UNPROCESSED;

import java.time.OffsetDateTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AccessCardEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(AccessCardEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void hasValidBuilderMethods() {

		final var created = now();
		final var modified = now().plusDays(1);
		final var processed = now().plusDays(2);
		final var orderCreated = now().plusDays(3);
		final var firstName = "firstName";
		final var lastName = "lastName";
		final var id = 1L;
		final var flowInstanceId = "flowInstanceId";
		final var referenceCode = "referenceCode";
		final var referenceCodeId = "referenceCodeId";
		final var referenceName = "referenceName";
		final var status = UNPROCESSED;
		final var statusMessage = "statusMessage";
		final var username = "username";
		final var photo = true;

		final var entity = AccessCardEntity.create()
			.withCreated(created)
			.withModified(modified)
			.withProcessed(processed)
			.withPosted(orderCreated)
			.withFirstName(firstName)
			.withLastName(lastName)
			.withId(id)
			.withFlowInstanceId(flowInstanceId)
			.withReferenceCode(referenceCode)
			.withReferenceCodeId(referenceCodeId)
			.withReferenceName(referenceName)
			.withStatus(status)
			.withStatusMessage(statusMessage)
			.withUsername(username)
			.withPhoto(photo);

		assertThat(entity).hasNoNullFieldsOrProperties();
		assertThat(entity.getCreated()).isEqualTo(created);
		assertThat(entity.getModified()).isEqualTo(modified);
		assertThat(entity.getProcessed()).isEqualTo(processed);
		assertThat(entity.getPosted()).isEqualTo(orderCreated);
		assertThat(entity.getFirstName()).isEqualTo(firstName);
		assertThat(entity.getLastName()).isEqualTo(lastName);
		assertThat(entity.getId()).isEqualTo(id);
		assertThat(entity.getFlowInstanceId()).isEqualTo(flowInstanceId);
		assertThat(entity.getReferenceCode()).isEqualTo(referenceCode);
		assertThat(entity.getReferenceCodeId()).isEqualTo(referenceCodeId);
		assertThat(entity.getReferenceName()).isEqualTo(referenceName);
		assertThat(entity.getStatus()).isEqualTo(status);
		assertThat(entity.getStatusMessage()).isEqualTo(statusMessage);
		assertThat(entity.getUsername()).isEqualTo(username);
		assertThat(entity.getPhoto()).isEqualTo(photo);
	}

	@ParameterizedTest
	@MethodSource("hasPhotoArguments")
	void hasPhoto(final Boolean value, final boolean expected) {
		assertThat(AccessCardEntity.create().withPhoto(value).hasPhoto()).isEqualTo(expected);
	}

	private static Stream<Arguments> hasPhotoArguments() {
		return Stream.of(
			Arguments.of(TRUE, true),
			Arguments.of(FALSE, false),
			Arguments.of(null, false));
	}

	@Test
	void hasNoDirtOnCreatedBean() {
		assertThat(new AccessCardEntity()).hasAllNullFieldsOrProperties();
		assertThat(AccessCardEntity.create()).hasAllNullFieldsOrProperties();
	}
}
