package nerds.studiousTestProject.payment.repository;

import nerds.studiousTestProject.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentKeyAndOrderId(String paymentKey, String orderId);
}
