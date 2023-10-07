package nerds.studiousTestProject.reservation.repository;

import io.lettuce.core.dynamic.annotation.Param;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<ReservationRecord> findAllByMemberId(Long memberId, Pageable pageable);

    @Query(value = "select r from ReservationRecord r " +
            "where r.room.id in (select ro.id from Room ro where ro.studycafe.id = :studycafeId) " +
            "and r.review.id is not null")
    List<ReservationRecord> findAllByStudycafeId(@Param("studycafeId") Long studycafeId);

    Optional<ReservationRecord> findByReviewId(Long reviewId);

    @Query("select r from ReservationRecord r join fetch r.room ro join ro.studycafe where r.id = :reservationId")
    Optional<ReservationRecord> findByIdWithPlace(@Param("reservationId") Long reservationRecordId);

}
