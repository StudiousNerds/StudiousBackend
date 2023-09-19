package nerds.studiousTestProject.payment.repository;

import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReservationRecord(ReservationRecord reservationRecord);
    Optional<Payment> findByOrderId(String orderId);
}
