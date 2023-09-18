package nerds.studiousTestProject.convenience.repository;

import nerds.studiousTestProject.convenience.entity.ConvenienceRecord;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConvenienceRecordRepository extends JpaRepository<ConvenienceRecord, Long> {

    Optional<ConvenienceRecord> findByReservationRecord(ReservationRecord reservationRecord);

}
