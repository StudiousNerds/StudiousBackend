package nerds.studiousTestProject.payment.repository;

import io.lettuce.core.dynamic.annotation.Param;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReservationRecord(ReservationRecord reservationRecord);
    Optional<Payment> findByOrderId(String orderId);

    List<Payment> findAllByReservationRecord(ReservationRecord reservationRecord);

    boolean existsByReservationRecord(ReservationRecord reservationRecord);

    @Query("select sum(p.price) from Payment p where p.reservationRecord = :reservationRecord")
    int findTotalPriceByReservationId(@Param("reservationRecord") ReservationRecord reservationRecord);
}
