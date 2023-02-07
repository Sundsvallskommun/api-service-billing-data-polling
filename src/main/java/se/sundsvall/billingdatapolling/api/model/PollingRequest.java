package se.sundsvall.billingdatapolling.api.model;

import java.time.LocalDate;
import java.util.Objects;

import javax.validation.constraints.PastOrPresent;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Polling request model")
public class PollingRequest {

	@DateTimeFormat(iso = ISO.DATE)
	@Schema(description = "Polling from date. Format is YYYY-MM-DD. If not provided, the last posted-date minus 1 day on previously stored orders, will be used.", example = "2023-01-01")
	@PastOrPresent
	private LocalDate fromDate;

	@DateTimeFormat(iso = ISO.DATE)
	@Schema(description = "Polling to date. Format is YYYY-MM-DD. If not provided, 'now' will be used.", example = "2023-01-02")
	@PastOrPresent
	private LocalDate toDate;

	public static PollingRequest create() {
		return new PollingRequest();
	}

	public LocalDate getFromDate() {
		return fromDate;
	}

	public void setFromDate(final LocalDate fromDate) {
		this.fromDate = fromDate;
	}

	public PollingRequest withFromDate(final LocalDate fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	public LocalDate getToDate() {
		return toDate;
	}

	public void setToDate(final LocalDate toDate) {
		this.toDate = toDate;
	}

	public PollingRequest withToDate(final LocalDate toDate) {
		this.toDate = toDate;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fromDate, toDate);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final var other = (PollingRequest) obj;
		return Objects.equals(fromDate, other.fromDate) && Objects.equals(toDate, other.toDate);
	}

	@Override
	public String toString() {
		final var builder = new StringBuilder();
		builder.append("PollingRequest [fromDate=").append(fromDate).append(", toDate=").append(toDate).append("]");
		return builder.toString();
	}
}
