package nerds.studiousTestProject.payment.service;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.payment.dto.PaymentRequest;
import nerds.studiousTestProject.payment.dto.PaymentResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PaymentService {

    public PaymentResponse createPaymentResponse(PaymentRequest paymentRequest) {
        return PaymentResponse.builder()
                .amount(paymentRequest.getReservation().getPrice())
                .orderId(String.valueOf(UUID.randomUUID()))
                .orderName(paymentRequest.getUser().getName())
                .successUrl("http://localhost:8080/studious/payment/ssuccess")
                .failUrl("http://localhost:8080/studious/payments/fail")
                .build();
    }

}
