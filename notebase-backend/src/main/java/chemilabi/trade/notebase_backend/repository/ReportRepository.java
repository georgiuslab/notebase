package chemilabi.trade.notebase_backend.repository;

import chemilabi.trade.notebase_backend.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}