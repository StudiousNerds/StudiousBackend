package nerds.studiousTestProject.reservation.repository;

import io.lettuce.core.dynamic.annotation.Param;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRecordRepository extends JpaRepository<ReservationRecord, Long>, ReservationRecordRepositoryCustom {
    @Query(value = "select r.startTime, r.endTime " +
            "from ReservationRecord r " +
            "where r.date = :date and r.room.id = :roomId " +
            "and r.status = nerds.studiousTestProject.reservation.entity.ReservationStatus.CONFIRMED")
    List<Object[]> findAllReservedTime(@Param("date")LocalDate date, @Param("roomId")Long roomId);

    List<ReservationRecord> findAllByRoomId(Long roomId);
    Optional<ReservationRecord> findByOrderId(String orderId);
    List<ReservationRecord> findAllByMemberId(Long memberId);

    @Query(value = "select r from ReservationRecord r " +
            "where r.room.id in (select ro.id from Room ro where ro.studycafe.id = :studycafeId) " +
            "and r.review.id is not null")
    List<ReservationRecord> findAllByStudycafeId(@Param("studycafeId") Long studycafeId);
}
