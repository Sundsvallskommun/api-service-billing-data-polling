package se.sundsvall.billingdatapolling.integration.db.model;

import static javax.persistence.EnumType.STRING;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import se.sundsvall.billingdatapolling.integration.db.listener.AccessCardEntityListener;
import se.sundsvall.billingdatapolling.integration.db.model.enums.Status;

@Entity
@Table(name = "access_card",
	indexes = {
		@Index(name = "access_card_flow_instance_id_idx", columnList = "flow_instance_id")
	})
@EntityListeners(AccessCardEntityListener.class)
public class AccessCardEntity implements Serializable {

	private static final long serialVersionUID = -8985339297689851236L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "flow_instance_id", nullable = false)
	private String flowInstanceId;

	@Column(name = "reference_code", nullable = false)
	private String referenceCode;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "username")
	private String username;

	@Column(name = "created")
	private OffsetDateTime created;

	@Column(name = "modified")
	private OffsetDateTime modified;

	@Column(name = "processed")
	private OffsetDateTime processed;

	@Column(name = "status", nullable = false)
	@Enumerated(STRING)
	private Status status;

	@Column(name = "photo", nullable = false)
	private Boolean photo;

	public static AccessCardEntity create() {
		return new AccessCardEntity();
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public AccessCardEntity withId(final Long id) {
		this.id = id;
		return this;
	}

	public String getFlowInstanceId() {
		return flowInstanceId;
	}

	public void setFlowInstanceId(final String flowInstanceId) {
		this.flowInstanceId = flowInstanceId;
	}

	public AccessCardEntity withFlowInstanceId(final String flowInstanceId) {
		this.flowInstanceId = flowInstanceId;
		return this;
	}

	public String getReferenceCode() {
		return referenceCode;
	}

	public void setReferenceCode(final String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public AccessCardEntity withReferenceCode(final String referenceCode) {
		this.referenceCode = referenceCode;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public AccessCardEntity withFirstName(final String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public AccessCardEntity withLastName(final String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public AccessCardEntity withUsername(final String username) {
		this.username = username;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public AccessCardEntity withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(final OffsetDateTime modified) {
		this.modified = modified;
	}

	public AccessCardEntity withModified(final OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	public OffsetDateTime getProcessed() {
		return processed;
	}

	public void setProcessed(final OffsetDateTime processed) {
		this.processed = processed;
	}

	public AccessCardEntity withProcessed(final OffsetDateTime processed) {
		this.processed = processed;
		return this;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(final Status status) {
		this.status = status;
	}

	public AccessCardEntity withStatus(final Status status) {
		this.status = status;
		return this;
	}

	public Boolean getPhoto() {
		return photo;
	}

	public void setPhoto(final Boolean photo) {
		this.photo = photo;
	}

	public AccessCardEntity withPhoto(final Boolean photo) {
		this.photo = photo;
		return this;
	}

	public boolean hasPhoto() {
		return isTrue(this.photo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(created, firstName, flowInstanceId, id, lastName, modified, photo, processed, referenceCode, status, username);
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
		final var other = (AccessCardEntity) obj;
		return Objects.equals(created, other.created) && Objects.equals(firstName, other.firstName) && Objects.equals(flowInstanceId, other.flowInstanceId) && Objects.equals(id, other.id) && Objects.equals(lastName, other.lastName) && Objects.equals(
			modified, other.modified) && Objects.equals(photo, other.photo) && Objects.equals(processed, other.processed) && Objects.equals(referenceCode, other.referenceCode) && status == other.status && Objects.equals(username, other.username);
	}

	@Override
	public String toString() {
		final var builder = new StringBuilder();
		builder.append("AccessCardEntity [id=").append(id).append(", flowInstanceId=").append(flowInstanceId).append(", referenceCode=").append(referenceCode).append(", firstName=").append(firstName).append(", lastName=").append(lastName).append(
			", username=").append(username).append(", created=").append(created).append(", modified=").append(modified).append(", processed=").append(processed).append(", status=").append(status).append(", photo=").append(photo).append("]");
		return builder.toString();
	}
}
