package chemilabi.trade.notebase_backend.repository;

import chemilabi.trade.notebase_backend.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}