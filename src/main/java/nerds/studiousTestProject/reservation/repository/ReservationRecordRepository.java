package nerds.studiousTestProject.reservation.repository;

import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query(value = "select r from ReservationRecord r " +
            "where r.room.id in (select ro.id from Room ro where ro.studycafe.id = :studycafeId) " +
            "and r.review.id is not null")
    List<ReservationRecord> findAllByStudycafeId(@Param("studycafeId") Long studycafeId);

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
