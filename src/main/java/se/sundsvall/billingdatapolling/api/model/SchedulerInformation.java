package se.sundsvall.billingdatapolling.api.model;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Scheduler information model")
public class SchedulerInformation {

	@Schema(description = "Scheduler enabled", example = "false")
	private boolean enabled;

	@Schema(description = "Scheduler expression", example = "0 0 13 * * 7")
	private String expression;

	@Schema(description = "Scheduler human readable description", example = "At 13:00, only on Sunday")
	private String description;

	public static SchedulerInformation create() {
		return new SchedulerInformation();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled( boolean enabled) {
		this.enabled = enabled;
	}

	public SchedulerInformation withEnabled( boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression( String expression) {
		this.expression = expression;
	}

	public SchedulerInformation withExpression( String expression) {
		this.expression = expression;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription( String description) {
		this.description = description;
	}

	public SchedulerInformation withDescription( String description) {
		this.description = description;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, enabled, expression);
	}

	@Override
	public boolean equals( Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof SchedulerInformation)) {
			return false;
		}
		 var other = (SchedulerInformation) obj;
		return Objects.equals(description, other.description) && enabled == other.enabled && Objects.equals(expression, other.expression);
	}

	@Override
	public String toString() {
		 var builder = new StringBuilder();
		builder.append("SchedulerInformation [enabled=").append(enabled).append(", expression=").append(expression).append(", description=").append(description).append("]");
		return builder.toString();
	}
}
