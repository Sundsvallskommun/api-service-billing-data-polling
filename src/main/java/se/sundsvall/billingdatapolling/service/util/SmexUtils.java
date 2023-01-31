package se.sundsvall.billingdatapolling.service.util;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import generated.se.sundsvall.smex.skreferensnummer.SKReferensNummer;

public class SmexUtils {

	private SmexUtils() {}

	/**
	 * Get the SKReferensNummer that matches the parameter, or null if nothing was found.
	 *
	 * 
	 * 
	 * @return SKReferensNummer or null if nothing was found.
	 */
	public static SKReferensNummer filterByReferenceCode( List<SKReferensNummer> skReferensNummerList,  Long referenceCode) {
		return Optional.ofNullable(skReferensNummerList).orElse(emptyList()).stream()
			.filter(Objects::nonNull)
			.filter(skReferensNummer -> Objects.equals(skReferensNummer.getREFKODID(), referenceCode))
			.findFirst()
			.orElse(null);
	}
}
