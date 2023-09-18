package nerds.studiousTestProject.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.payment.dto.callback.request.DepositCallbackRequest;
import nerds.studiousTestProject.payment.dto.virtual.response.VirtualAccountInfoResponse;
import nerds.studiousTestProject.payment.util.totoss.ConfirmSuccessRequest;
import nerds.studiousTestProject.payment.util.fromtoss.PaymentResponseFromToss;
import nerds.studiousTestProject.payment.util.totoss.CancelRequest;
import nerds.studiousTestProject.payment.dto.confirm.response.ConfirmFailResponse;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.payment.repository.PaymentRepository;
import nerds.studiousTestProject.payment.util.PaymentGenerator;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static nerds.studiousTestProject.common.exception.ErrorCode.INVALID_PAYMENT_SECRET;
import static nerds.studiousTestProject.common.exception.ErrorCode.MISMATCH_PAYMENT_METHOD;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_PAYMENT;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_RESERVATION_RECORD;
import static nerds.studiousTestProject.payment.entity.PaymentMethod.가상계좌;
import static nerds.studiousTestProject.payment.entity.PaymentStatus.CANCELED;
import static nerds.studiousTestProject.payment.entity.PaymentStatus.DONE;
import static nerds.studiousTestProject.payment.entity.PaymentStatus.WAITING_FOR_DEPOSIT;


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
    public void confirmPayToToss(String orderId, String paymentKey, Integer amount) {
        PaymentResponseFromToss responseFromToss = paymentGenerator.requestToToss(ConfirmSuccessRequest.of(orderId,amount,paymentKey), CONFIRM_URI);
        Payment payment = findByOrderId(orderId);
        payment.complete(responseFromToss.toPayment());
        log.info("success payment ! payment status is {} and method is {}", responseFromToss.getStatus(), responseFromToss.getMethod());
        ReservationRecord reservationRecord = findReservationRecordByOrderId(orderId);
        reservationRecord.completePay();//결제 완료로 상태 변경
    }

    @Transactional
    public VirtualAccountInfoResponse virtualAccount(String orderId, String paymentKey, Integer amount) {
        PaymentResponseFromToss responseFromToss = paymentGenerator.requestToToss(ConfirmSuccessRequest.of(orderId, amount, paymentKey), CONFIRM_URI);
        Payment payment = paymentRepository.save(responseFromToss.toVitualAccountPayment());
        log.info("success payment ! payment status is {} and method is {}", responseFromToss.getStatus(), responseFromToss.getMethod());
        if (!payment.getMethod().equals(가상계좌.name())) {
            throw new BadRequestException(MISMATCH_PAYMENT_METHOD);
        }
        return VirtualAccountInfoResponse.from(payment);
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
        Payment payment = findByReservationRecord(reservationRecord);
        validPaymentMethod(cancelRequest, payment);
        PaymentResponseFromToss responseFromToss = paymentGenerator.requestToToss(cancelRequest, String.format(CANCEL_URI, payment.getPaymentKey()));
        payment.canceled(responseFromToss);
        reservationRecord.canceled(); //결제 취소 상태로 변경
    }

    private void validPaymentMethod(CancelRequest cancelRequest, Payment payment) {
        if (payment.getMethod().equals(가상계좌)) {
            cancelRequest.getRefundReceiveAccount().validRefundVirtualAccountPay();
        }
    }

    private Payment findByReservationRecord(ReservationRecord reservationRecord) {
        return paymentRepository.findByReservationRecord(reservationRecord).orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));
    }

    private ReservationRecord findReservationById(Long reservationId) {
        return reservationRecordRepository.findById(reservationId).orElseThrow(()-> new NotFoundException(NOT_FOUND_RESERVATION_RECORD));
    }

    public void processDepositByStatus(DepositCallbackRequest depositCallbackRequest) {
        Payment payment = findByOrderId(depositCallbackRequest.getOrderId());
        String status = depositCallbackRequest.getStatus();
        ReservationRecord reservationRecord = findReservationRecordByPayment(payment);
        if(isDepositError(payment, status)){ // 입금 오류
            //입금 오류에 관한 알림 전송
            reservationRecord.depositError();
        }
        if (status.equals(DONE.name())) { // 입금 완료
            validPaymentSecret(depositCallbackRequest, payment);
            reservationRecord.completeDeposit();
            payment.updateCompleteTime(depositCallbackRequest.getCreatedAt());
        }
        if (status.equals(CANCELED.name())) { // 입금 전 취소 & 결제 취소
            reservationRecord.canceled();
        }
        payment.updateStatus(status);
    }

    private boolean isDepositError(Payment payment, String status) {
        return status.equals(WAITING_FOR_DEPOSIT.name()) && payment.getStatus().equals(DONE);
    }

    private void validPaymentSecret(DepositCallbackRequest depositCallbackRequest, Payment payment) {
        if (!payment.getSecret().equals(depositCallbackRequest.getSecret())) {
            throw new BadRequestException(INVALID_PAYMENT_SECRET);
        }
    }

    private Payment findByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId).orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));
    }

}
