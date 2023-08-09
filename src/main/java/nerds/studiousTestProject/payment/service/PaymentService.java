package nerds.studiousTestProject.payment.service;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.payment.util.totoss.ConfirmSuccessRequest;
import nerds.studiousTestProject.payment.util.fromtoss.PaymentResponseFromToss;
import nerds.studiousTestProject.payment.util.totoss.CancelRequest;
import nerds.studiousTestProject.payment.dto.cancel.response.CancelResponse;
import nerds.studiousTestProject.payment.dto.confirm.response.ConfirmFailResponse;
import nerds.studiousTestProject.payment.dto.confirm.response.ConfirmSuccessResponse;
import nerds.studiousTestProject.payment.dto.confirm.response.ReservationInfo;
import nerds.studiousTestProject.payment.dto.confirm.response.ReserveUserInfo;
import nerds.studiousTestProject.payment.dto.request.request.PaymentRequest;
import nerds.studiousTestProject.payment.dto.request.response.PaymentResponse;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.payment.repository.PaymentRepository;
import nerds.studiousTestProject.payment.util.PaymentGenerator;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.service.ReservationRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_PAYMENT;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final ReservationRecordService reservationRecordService;
    private final PaymentRepository paymentRepository;
    private final PaymentGenerator paymentGenerator;

    private static final String REQUEST_SUCCESS_URI = "http://localhost:8080/studious/payments/success";
    private static final String REQUEST_FAIL_URI = "http://localhost:8080/studious/payments/success";
    private static final String CONFIRM_URI = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String CANCEL_URI = "https://api.tosspayments.com/v1/payments/%s/cancel";

    public PaymentResponse createPaymentResponse(PaymentRequest paymentRequest, String orderId) {
        return PaymentResponse.builder()
                .amount(paymentRequest.getReservation().getPrice())
                .orderId(orderId)
                .orderName(paymentRequest.getUser().getName())
                .successUrl(REQUEST_SUCCESS_URI)
                .failUrl(REQUEST_FAIL_URI)
                .build();
    }

    @Transactional
    public ConfirmSuccessResponse confirmPayToToss(String orderId, String paymentKey, Integer amount) {
        ConfirmSuccessRequest request = ConfirmSuccessRequest.of(orderId,amount,paymentKey);
        PaymentResponseFromToss responseFromToss = paymentGenerator.requestToToss(request, CONFIRM_URI);
        Payment payment = paymentRepository.save(responseFromToss.toPayment());
        reservationRecordService.findByOrderId(orderId).completePay(payment);//결제 완료로 상태 변경
        return createPaymentConfirmResponse(responseFromToss);
    }

    public ConfirmSuccessResponse createPaymentConfirmResponse(PaymentResponseFromToss responseFromToss){
        ReservationRecord reservationRecord = reservationRecordService.findByOrderId(responseFromToss.getOrderId());
        return ConfirmSuccessResponse.builder()
                .reservationInfo(ReservationInfo.of(reservationRecord))
                .reserveUserInfo(ReserveUserInfo.of(reservationRecord))
                .build();
    }

    /*
     실패시 저장되었던 예약내역 삭제, 실패 정보 반환
     */
    @Transactional
    public ConfirmFailResponse confirmFail(String message, String orderId){
        reservationRecordService.deleteByOrderId(orderId);
        return ConfirmFailResponse.builder()
                .message(message)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    public void cancel(CancelRequest cancelRequest, Long reservationId){
        ReservationRecord reservationRecord = reservationRecordService.findById(reservationId);
        requestCancelToToss(cancelRequest, reservationRecord.getPayment().getPaymentKey());
        reservationRecordService.cancel(reservationId); //결제 취소 상태로 변경
    }
    @Transactional
    public List<CancelResponse> requestCancelToToss(CancelRequest cancelRequest, String paymentKey){
        PaymentResponseFromToss responseFromToss = paymentGenerator.requestToToss(cancelRequest, String.format(CANCEL_URI, paymentKey));
        List<CancelResponse> cancelResponses = new ArrayList<>();
        responseFromToss.getCancels().stream().forEach(cancel -> cancelResponses.add(CancelResponse.of(cancel)));
        deletePaymentByCancel(responseFromToss);
        return cancelResponses;
    }

    private void deletePaymentByCancel(PaymentResponseFromToss responseFromToss) {
        Payment payment = paymentRepository.findByPaymentKeyAndOrderId(
                        responseFromToss.getPaymentKey(),
                        responseFromToss.getOrderId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));
        paymentRepository.delete(payment);
    }

}
