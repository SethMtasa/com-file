package prac.com_file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import prac.com_file.model.AuditLog;

/**
 * Repository interface for the AuditLog entity.
 * This is a crucial addition to enable Spring Data JPA to correctly
 * bootstrap and recognize the entities, including those inheriting from
 * a MappedSuperclass like BaseEntity.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}

