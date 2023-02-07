package se.sundsvall.billingdatapolling.service.util;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import generated.se.sundsvall.smex.skreferensnummer.SKReferensNummer;

class SmexUtilsTest {

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void filterByReferenceCode(final int index) {

		final var result = SmexUtils.filterByReferenceCode(createTestList(), "RefCode" + index);

		assertThat(result).isNotNull();
		assertThat(result.getANVNAMN()).isEqualTo("Name" + index);
		assertThat(result.getBESKRIVNING()).isEqualTo("Description" + index);
		assertThat(result.getREFKOD()).isEqualTo("RefCode" + index);
		assertThat(result.getREFKODID()).isEqualTo(index);
	}

	@Test
	void filterByReferenceCodeWhenNotFound() {
		assertThat(SmexUtils.filterByReferenceCode(createTestList(), "666")).isNull();
	}

	@Test
	void filterByReferenceCodeWhenListIsEmpty() {
		assertThat(SmexUtils.filterByReferenceCode(emptyList(), "RefCode1")).isNull();
	}

	@Test
	void filterByReferenceCodeWhenListIsNull() {
		assertThat(SmexUtils.filterByReferenceCode(null, "RefCode1")).isNull();
	}

	private final List<SKReferensNummer> createTestList() {
		return List.of(
			new SKReferensNummer()
				.ANV_NAMN("Name1")
				.BESKRIVNING("Description1")
				.REFKOD("RefCode1")
				.REFKOD_ID(1L),
			new SKReferensNummer()
				.ANV_NAMN("Name2")
				.BESKRIVNING("Description2")
				.REFKOD("RefCode2")
				.REFKOD_ID(2L),
			new SKReferensNummer()
				.ANV_NAMN("Name3")
				.BESKRIVNING("Description3")
				.REFKOD("RefCode3")
				.REFKOD_ID(3L),
			new SKReferensNummer()
				.ANV_NAMN("Name4")
				.BESKRIVNING("Description4")
				.REFKOD("RefCode4")
				.REFKOD_ID(4L),
			new SKReferensNummer()
				.ANV_NAMN("Name5")
				.BESKRIVNING("Description5")
				.REFKOD("RefCode5")
				.REFKOD_ID(5L));
	}
}
