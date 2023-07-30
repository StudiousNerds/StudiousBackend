package nerds.studiousTestProject.reservation.repository;

import io.lettuce.core.dynamic.annotation.Param;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRecordRepository extends JpaRepository<ReservationRecord, Long> {
    @Query(value = "select r from ReservationRecord r where r.date = :date and r.room = :roomId and r.reservationStatus = 'RESERVED'", nativeQuery = true)
    public List<Object[]> findAllReservedTime(@Param("date")LocalDate date, @Param("roomId")Long roomId);
}
