package nerds.studiousTestProject.convenience.repository;

import nerds.studiousTestProject.convenience.entity.ConvenienceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConvenienceRecordRepository extends JpaRepository<ConvenienceRecord, Long> {
}
