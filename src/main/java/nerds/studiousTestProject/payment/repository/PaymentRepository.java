package nerds.studiousTestProject.payment.repository;

import nerds.studiousTestProject.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
