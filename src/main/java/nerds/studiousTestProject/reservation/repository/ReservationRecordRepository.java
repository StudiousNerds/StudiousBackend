package nerds.studiousTestProject.reservation.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReservationRecordRepository extends JpaRepository<ReservationRecord, Long> {

    public Optional<ReservationRecord> findByOrderId(String orderId);

}
