package nerds.studiousTestProject.reservation.repository;

import io.lettuce.core.dynamic.annotation.Param;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRecordRepository extends JpaRepository<ReservationRecord, Long> {
    @Query(value = "select r.startTime, r.endTime " +
            "from ReservationRecord r " +
            "where r.date = :date and r.room.id = :roomId " +
            "and r.reservationStatus = 0")
    List<Object[]> findAllReservedTime(@Param("date")LocalDate date, @Param("roomId")Long roomId);

    @Query("select r from ReservationRecord r where r.room.id = :roomId")
    List<ReservationRecord> findAllByRoomId(@Param("roomId")Long roomId);
}
