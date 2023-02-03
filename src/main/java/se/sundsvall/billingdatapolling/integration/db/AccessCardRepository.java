package se.sundsvall.billingdatapolling.integration.db;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.billingdatapolling.integration.db.model.AccessCardEntity;
import se.sundsvall.billingdatapolling.integration.db.model.enums.Status;

@Transactional
@CircuitBreaker(name = "AccessCardRepository")
public interface AccessCardRepository extends JpaRepository<AccessCardEntity, Long> {

	/**
	 * Find by flowInstanceId.
	 *
	 * @param flowInstanceId
	 * @return An optional AccessCardEntity.
	 */
	Optional<AccessCardEntity> findByFlowInstanceId(String flowInstanceId);

	/**
	 * Find by status list.
	 *
	 * @param a List of Status:es to filter on.
	 * @return A List of AccessCardEntity
	 */
	List<AccessCardEntity> findByStatusIn(List<Status> statusList);

	/**
	 * Find the last posted order among the AccessCardEntity objects.
	 *
	 * @param the AccessCardEntity that was last posted.
	 * @return An optional AccessCardEntity.
	 */
	Optional<AccessCardEntity> findFirstByOrderByPostedDesc();
}
