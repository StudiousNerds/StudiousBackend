package nerds.studiousTestProject.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.payment.util.totoss.ConfirmSuccessRequest;
import nerds.studiousTestProject.payment.util.fromtoss.PaymentResponseFromToss;
import nerds.studiousTestProject.payment.util.totoss.CancelRequest;
import nerds.studiousTestProject.payment.dto.cancel.response.CancelResponse;
import nerds.studiousTestProject.payment.dto.confirm.response.ConfirmFailResponse;
import nerds.studiousTestProject.payment.dto.confirm.response.ConfirmSuccessResponse;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.payment.repository.PaymentRepository;
import nerds.studiousTestProject.payment.util.PaymentGenerator;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_PAYMENT;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_RESERVATION_RECORD;


@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentGenerator paymentGenerator;
    private final ReservationRecordRepository reservationRecordRepository;

    private static final String CONFIRM_URI = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String CANCEL_URI = "https://api.tosspayments.com/v1/payments/%s/cancel";


    @Transactional
    public ConfirmSuccessResponse confirmPayToToss(String orderId, String paymentKey, Integer amount) {
        PaymentResponseFromToss responseFromToss = paymentGenerator.requestToToss(ConfirmSuccessRequest.of(orderId,amount,paymentKey), CONFIRM_URI);
        Payment payment = paymentRepository.save(responseFromToss.toPayment());
        log.info("success payment ! payment status is {} and method is {}", responseFromToss.getStatus(), responseFromToss.getMethod());
        ReservationRecord reservationRecord = findReservationRecordByOrderId(orderId);
        reservationRecord.completePay(payment);//결제 완료로 상태 변경
        return ConfirmSuccessResponse.from(reservationRecord);
    }

    private ReservationRecord findReservationRecordByOrderId(String orderId) {
        return reservationRecordRepository.findByOrderId(orderId).orElseThrow(() -> new NotFoundException(NOT_FOUND_RESERVATION_RECORD));
    }

    /*
     실패시 저장되었던 예약내역 삭제, 실패 정보 반환
     */
    @Transactional
    public ConfirmFailResponse confirmFail(String message, String orderId){
        reservationRecordRepository.delete(findReservationRecordByOrderId(orderId));
        return ConfirmFailResponse.builder()
                .message(message)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    @Transactional
    public void cancel(CancelRequest cancelRequest, Long reservationId){
        ReservationRecord reservationRecord = findReservationById(reservationId);
        requestCancelToToss(cancelRequest, reservationRecord.getPayment().getPaymentKey());
        reservationRecord.canceled(); //결제 취소 상태로 변경
    }

    private List<CancelResponse> requestCancelToToss(CancelRequest cancelRequest, String paymentKey){
        PaymentResponseFromToss responseFromToss = paymentGenerator.requestToToss(cancelRequest, String.format(CANCEL_URI, paymentKey));
        List<CancelResponse> cancelResponses = responseFromToss.getCancels().stream().map(CancelResponse::of).toList();
        cancelPayment(responseFromToss);
        return cancelResponses;
    }

    private void cancelPayment(PaymentResponseFromToss responseFromToss) {
        Payment payment = paymentRepository.findByPaymentKeyAndOrderId(
                        responseFromToss.getPaymentKey(),
                        responseFromToss.getOrderId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));
        payment.canceled(responseFromToss);
    }

    private ReservationRecord findReservationById(Long reservationId) {
        return reservationRecordRepository.findById(reservationId).orElseThrow(()-> new NotFoundException(NOT_FOUND_RESERVATION_RECORD));
    }

}
