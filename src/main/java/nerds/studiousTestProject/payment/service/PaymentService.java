package nerds.studiousTestProject.payment.service;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.payment.dto.confirm.*;
import nerds.studiousTestProject.payment.dto.request.PaymentRequest;
import nerds.studiousTestProject.payment.dto.request.PaymentResponse;
import nerds.studiousTestProject.reservationRecord.entity.ReservationRecord;
import nerds.studiousTestProject.reservationRecord.service.ReservationRecordService;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

import static nerds.studiousTestProject.payment.PaymentConstant.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final WebClient webClient;
    private final ReservationRecordService reservationRecordService;

    public PaymentResponse createPaymentResponse(PaymentRequest paymentRequest, String orderId) {
        return PaymentResponse.builder()
                .amount(paymentRequest.getReservation().getPrice())
                .orderId(orderId)
                .orderName(paymentRequest.getUser().getName())
                .successUrl("http://localhost:8080/studious/payments/success")
                .failUrl("http://localhost:8080/studious/payments/fail")
                .build();
    }

    @Transactional
    public ConfirmSuccessResponse confirmPayToToss(String orderId, String paymentKey, int amount) {
        ConfirmSuccessRequest request = ConfirmSuccessRequest.builder()
                .amount(amount)
                .orderId(orderId)
                .paymentKey(paymentKey)
                .build();
        String encodedAuth = Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes());
        ConfirmSuccessResponseFromToss responseFromToss = webClient.method(HttpMethod.POST)
                .uri(CONFIRM_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + encodedAuth)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ConfirmSuccessResponseFromToss.class)
                .block();
        return createPaymentConfirmResponse(responseFromToss);
    }

    public ConfirmSuccessResponse createPaymentConfirmResponse(ConfirmSuccessResponseFromToss responseFromToss){
        ReservationRecord reservationRecord = reservationRecordService.findByOrderId(responseFromToss.getOrderId());
        ReserveUserInfo reserveUserInfo = ReserveUserInfo.builder()
                .name(reservationRecord.getName())
                .phoneNumber(reservationRecord.getPhoneNumber())
                .request(reservationRecord.getRequest())
                .build();
        ReservationInfo reservationInfo = ReservationInfo.builder()
                .reserveDate(reservationRecord.getDate())
                .roomName(reservationRecord.getRoom().getName())
                .studycafeName(reservationRecord.getRoom().getStudycafe().getName())
                .startTime(reservationRecord.getStartTime())
                .endTime(reservationRecord.getEndTime())
                .usingTime(reservationRecord.getDuration())
                .build();
        return ConfirmSuccessResponse.builder()
                .reservationInfo(reservationInfo)
                .reserveUserInfo(reserveUserInfo)
                .build();
    }

}
