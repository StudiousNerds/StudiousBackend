package nerds.studiousTestProject.reservation.repository;

import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRecordRepository extends JpaRepository<ReservationRecord, Long>, ReservationRecordRepositoryCustom {
    @Query(value = "select r.startTime, r.endTime " +
            "from ReservationRecord r " +
            "where r.date = :date and r.room.id = :roomId " +
            "and r.status = nerds.studiousTestProject.reservation.entity.ReservationStatus.CONFIRMED")
    List<List<LocalTime>> findAllReservedTime(@Param("date")LocalDate date, @Param("roomId")Long roomId);

    @Query(value = "select r.id, date, using_time, start_time, end_time, head_count, user_name, order_id, user_phone_number, request, status, member_id, room_id, review_id " +
            "from reservation_record r " +
            "where r.date >= :date and r.id = :roomId " +
            "and r.status = 'CONFIRMED' " +
            "group by r.date", nativeQuery = true)
    List<ReservationRecord> findAllByFutureReservedTimeGroupingByDate(@Param("date")LocalDate date, @Param("roomId")Long roomId);

    List<ReservationRecord> findAllByRoomId(Long roomId);

    @Query(value = "select r from ReservationRecord r " +
            "where r.room.id in (select ro.id from Room ro where ro.studycafe.id = :studycafeId) " +
            "and r.review.id is not null")
    List<ReservationRecord> findAllByStudycafeId(@Param("studycafeId") Long studycafeId);

    List<ReservationRecord> findAllByRoomIdAndDate(Long roomId, LocalDate date);

    Page<ReservationRecord> findAllByMember(Pageable pageable, Member member);

    Optional<ReservationRecord> findByReviewId(Long reviewId);

    @Query("select r from ReservationRecord r join fetch r.room o join fetch o.studycafe where r.id = :reservationRecordId")
    Optional<ReservationRecord> findByIdWithPlace(@Param("reservationRecordId") Long reservationRecordId);

    @Query("select r from ReservationRecord r join fetch r.payment join fetch r.room o join fetch o.studycafe where r.id = :reservationRecordId")
    Optional<ReservationRecord> findByIdWithPaymentAndPlace(@Param("reservationRecordId") Long reservationRecordId);

    @Query("select r from ReservationRecord r join fetch r.room o join fetch o.studycafe where r.payment.id = :paymentId")
    Optional<ReservationRecord> findByPaymentWithPlace(@Param("paymentId") Long paymentId);

    Optional<ReservationRecord> findByPayment(Payment payment);

    @Query("select r from ReservationRecord r join fetch r.payment where r.id = :reservationRecordId")
    Optional<ReservationRecord> findByIdWithPayment(@Param("reservationRecordId") Long reservationRecordId);
}
