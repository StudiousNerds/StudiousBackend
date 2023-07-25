package nerds.studiousTestProject.payment.service;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.payment.dto.confirm.PaymentConfirmResponseFromToss;
import nerds.studiousTestProject.payment.dto.confirm.PaymentConfirmRequest;
import nerds.studiousTestProject.payment.dto.request.PaymentRequest;
import nerds.studiousTestProject.payment.dto.request.PaymentResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import static nerds.studiousTestProject.payment.PaymentConstant.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final WebClient webClient;
    public PaymentResponse createPaymentResponse(PaymentRequest paymentRequest) {
        return PaymentResponse.builder()
                .amount(paymentRequest.getReservation().getPrice())
                .orderId(String.valueOf(UUID.randomUUID()))
                .orderName(paymentRequest.getUser().getName())
                .successUrl("http://localhost:8080/studious/payments/success")
                .failUrl("http://localhost:8080/studious/payments/fail")
                .build();
    }

    @Transactional
    public PaymentConfirmResponseFromToss confirmPay(String orderId, String paymentKey, int amount) throws IOException, InterruptedException {
        PaymentConfirmRequest request = PaymentConfirmRequest.builder()
                .amount(amount)
                .orderId(orderId)
                .paymentKey(paymentKey)
                .build();
        String encodedAuth = Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes());
        return webClient.method(HttpMethod.POST)
                .uri(CONFIRM_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + encodedAuth)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentConfirmResponseFromToss.class)
                .block();
    }

}
