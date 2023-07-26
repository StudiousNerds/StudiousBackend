package nerds.studiousTestProject.payment.service;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.payment.dto.cancel.CancelRequest;
import nerds.studiousTestProject.payment.dto.cancel.CancelResponse;
import nerds.studiousTestProject.payment.dto.confirm.*;
import nerds.studiousTestProject.payment.dto.request.PaymentRequest;
import nerds.studiousTestProject.payment.dto.request.PaymentResponse;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.reservationRecord.entity.ReservationRecord;
import nerds.studiousTestProject.reservationRecord.service.ReservationRecordService;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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
        String secreteKey = Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes());
        PaymentResponseFromToss responseFromToss = webClient.method(HttpMethod.POST)
                .uri(CONFIRM_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + secreteKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentResponseFromToss.class)
                .block();
        Payment payment = Payment.builder()
                .completeTime(responseFromToss.getRequestedAt())
                .type(responseFromToss.getType())
                .orderId(responseFromToss.getOrderId())
                .paymentKey(responseFromToss.getPaymentKey())
                .build();
        reservationRecordService.findByOrderId(orderId).completePay(payment);//결제 완료로 상태 변경
        return createPaymentConfirmResponse(responseFromToss);
    }

    public ConfirmSuccessResponse createPaymentConfirmResponse(PaymentResponseFromToss responseFromToss){
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

    /*
     실패시 저장되었던 예약내역 삭제, 실패 정보 반환
     */
    public ConfirmFailResponse confirmFail(String message, String orderId){
        reservationRecordService.deleteByOrderId(orderId);
        return ConfirmFailResponse.builder()
                .message(message)
                .statusCode(400)
                .build();
    }

    public List<CancelResponse> requestCancelToToss(CancelRequest cancelRequest){
        String secreteKey = Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes());

        PaymentResponseFromToss responseFromToss = webClient.method(HttpMethod.POST)
                .uri(CONFIRM_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + secreteKey)
                .bodyValue(cancelRequest)
                .retrieve()
                .bodyToMono(PaymentResponseFromToss.class)
                .block();
        List<CancelResponse> cancelResponses = new ArrayList<>();
        List<Cancels> cancels = responseFromToss.getCancels();
        for (Cancels cancel : cancels) {
            CancelResponse cancelResponse = CancelResponse.builder()
                    .canceledAt(cancel.getCanceledAt())
                    .cancelAmount(cancel.getCancelAmount())
                    .build();
            cancelResponses.add(cancelResponse);
        }
        return cancelResponses;
    }

}
