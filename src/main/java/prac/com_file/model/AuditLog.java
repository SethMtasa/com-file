package prac.com_file.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Objects;

/**
 * Represents an entry in the system's audit trail.
 */
@Entity
@Table(name = "audit_logs")
public class AuditLog extends BaseEntity {

    private String action;
    private String details;
    private String entityName;
    private Long entityId;

    // No-args constructor
    public AuditLog() {
    }

    // All-args constructor
    public AuditLog(String action, String details, String entityName, Long entityId) {
        this.action = action;
        this.details = details;
        this.entityName = entityName;
        this.entityId = entityId;
    }

    // Getters
    public String getAction() {
        return action;
    }

    public String getDetails() {
        return details;
    }

    public String getEntityName() {
        return entityName;
    }

    public Long getEntityId() {
        return entityId;
    }

    // Setters
    public void setAction(String action) {
        this.action = action;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    // Re-implementing equals and hashcode to be more robust for entities
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditLog auditLog = (AuditLog) o;
        // Access the inherited id field directly
        return Objects.equals(this.id, auditLog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
