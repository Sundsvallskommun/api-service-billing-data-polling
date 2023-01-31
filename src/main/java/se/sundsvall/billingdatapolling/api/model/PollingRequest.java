package se.sundsvall.billingdatapolling.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import java.time.LocalDate;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Polling request model")
public class PollingRequest {

	@DateTimeFormat(iso = ISO.DATE)
	@Schema(description = "Polling from date. Format is YYYY-MM-DD.", example = "2023-01-01", requiredMode = REQUIRED)
	@NotNull
	@PastOrPresent
	private LocalDate fromDate;

	@DateTimeFormat(iso = ISO.DATE)
	@Schema(description = "Polling to date. Format is YYYY-MM-DD.", example = "2023-01-02", requiredMode = REQUIRED)
	@NotNull
	@PastOrPresent
	private LocalDate toDate;

	public static PollingRequest create() {
		return new PollingRequest();
	}

	public LocalDate getFromDate() {
		return fromDate;
	}

	public void setFromDate( LocalDate fromDate) {
		this.fromDate = fromDate;
	}

	public PollingRequest withFromDate( LocalDate fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	public LocalDate getToDate() {
		return toDate;
	}

	public void setToDate( LocalDate toDate) {
		this.toDate = toDate;
	}

	public PollingRequest withToDate( LocalDate toDate) {
		this.toDate = toDate;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fromDate, toDate);
	}

	@Override
	public boolean equals( Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof PollingRequest)) {
			return false;
		}
		 var other = (PollingRequest) obj;
		return Objects.equals(fromDate, other.fromDate) && Objects.equals(toDate, other.toDate);
	}

	@Override
	public String toString() {
		 var builder = new StringBuilder();
		builder.append("PollingRequest [fromDate=").append(fromDate).append(", toDate=").append(toDate).append("]");
		return builder.toString();
	}
}
