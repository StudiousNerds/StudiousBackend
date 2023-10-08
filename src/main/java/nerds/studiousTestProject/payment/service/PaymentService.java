package nerds.studiousTestProject.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.convenience.repository.ConvenienceRecordRepository;
import nerds.studiousTestProject.payment.dto.callback.request.DepositCallbackRequest;
import nerds.studiousTestProject.payment.dto.virtual.response.VirtualAccountInfoResponse;
import nerds.studiousTestProject.payment.entity.PaymentStatus;
import nerds.studiousTestProject.payment.util.totoss.ConfirmSuccessRequest;
import nerds.studiousTestProject.payment.util.fromtoss.PaymentResponseFromToss;
import nerds.studiousTestProject.payment.util.totoss.CancelRequest;
import nerds.studiousTestProject.payment.dto.confirm.response.ConfirmFailResponse;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.payment.repository.PaymentRepository;
import nerds.studiousTestProject.payment.util.PaymentGenerator;
import nerds.studiousTestProject.reservation.dto.detail.response.ReservationDetailResponse;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.MISMATCH_CANCEL_PRICE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.MISMATCH_ORDER_ID;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.MISMATCH_PRICE;
import static nerds.studiousTestProject.payment.entity.PaymentMethod.VIRTUAL_ACCOUNT;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_PAYMENT_SECRET;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.MISMATCH_PAYMENT_METHOD;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_PAYMENT;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_RESERVATION_RECORD;
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
    private final ConvenienceRecordRepository convenienceRecordRepository;

    private static final String CONFIRM_URI = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String CANCEL_URI = "https://api.tosspayments.com/v1/payments/%s/cancel";


    @Transactional
    public ReservationDetailResponse confirmSuccess(String orderId, String paymentKey, Integer amount) {
        Payment payment = findByOrderIdWithReservationAndPlace(orderId);
        validConfirmRequest(orderId, amount, payment);
        PaymentResponseFromToss responseFromToss = paymentGenerator.requestToToss(new ConfirmSuccessRequest(orderId, paymentKey, amount), CONFIRM_URI);
        payment.complete(responseFromToss.toPayment());
        ReservationRecord reservationRecord = payment.getReservationRecord();
        reservationRecord.completePay();
        return ReservationDetailResponse.of(reservationRecord, payment);
    }

    private void validConfirmRequest(String orderId, Integer amount, Payment payment){
        if(!amount.equals(payment.getPrice())) throw new BadRequestException(MISMATCH_PRICE);
        if(!orderId.equals(payment.getOrderId())) throw new BadRequestException(MISMATCH_ORDER_ID);
    }

    @Transactional
    public VirtualAccountInfoResponse virtualAccount(String orderId, String paymentKey, Integer amount) {
        Payment payment = findByOrderId(orderId);
        validConfirmRequest(orderId, amount, payment);
        PaymentResponseFromToss responseFromToss = paymentGenerator.requestToToss(new ConfirmSuccessRequest(orderId, paymentKey, amount), CONFIRM_URI);
        validPaymentMethod(responseFromToss);
        payment.complete(responseFromToss.toVitualAccountPayment()); //complete 말고 다른 작명이 좋을 수도 있을 듯
        log.info("success payment ! payment status is {} and method is {}", responseFromToss.getStatus(), responseFromToss.getMethod());
        return VirtualAccountInfoResponse.from(payment);
    }

    private void validPaymentMethod(PaymentResponseFromToss responseFromToss) {
        if (!responseFromToss.getMethod().equals(VIRTUAL_ACCOUNT.getValue())) {
            throw new BadRequestException(MISMATCH_PAYMENT_METHOD);
        }
    }

    @Transactional
    public ConfirmFailResponse confirmFail(String message, String orderId){
        Payment payment = findByOrderIdWithReservation(orderId);
        ReservationRecord reservationRecord = payment.getReservationRecord();
        convenienceRecordRepository.findAllByPayment(payment).stream().forEach(convenience -> convenienceRecordRepository.delete(convenience));
        paymentRepository.delete(payment);
        if (!paymentRepository.existsByReservationRecord(reservationRecord)) {
            reservationRecordRepository.delete(reservationRecord);
        }
        return ConfirmFailResponse.of(message);
    }

    @Transactional
    public void cancel(CancelRequest cancelRequest, Long reservationId){
        ReservationRecord reservationRecord = findReservationById(reservationId);
        List<Payment> payments = findPaymentsByReservationRecord(reservationRecord);
        payments.stream().forEach(payment -> validPaymentMethod(cancelRequest, payment));
        //취소 금액 검증
        int totalCancelPrice = 0;
        for (Payment payment : payments) {
            PaymentResponseFromToss responseFromToss = paymentGenerator.requestToToss(cancelRequest, String.format(CANCEL_URI, payment.getPaymentKey()));
            payment.cancel(responseFromToss);
            totalCancelPrice += responseFromToss.getTotalAmount();
        }
        if (totalCancelPrice != payments.stream().mapToInt(Payment::getPrice).sum()) {
            throw new BadRequestException(MISMATCH_CANCEL_PRICE);
        }
        reservationRecord.canceled(); //결제 취소 상태로 변경
    }

    private List<Payment> findPaymentsByReservationRecord(ReservationRecord reservationRecord) {
        return paymentRepository.findAllByReservationRecord(reservationRecord);
    }

    private void validPaymentMethod(CancelRequest cancelRequest, Payment payment) {
        if (payment.getMethod().equals(VIRTUAL_ACCOUNT.getValue())) {
            cancelRequest.getRefundReceiveAccount().validRefundVirtualAccountPay();
        }
    }

    private ReservationRecord findReservationById(Long reservationId) {
        return reservationRecordRepository.findById(reservationId).orElseThrow(()-> new NotFoundException(NOT_FOUND_RESERVATION_RECORD));
    }

    public void processDepositByStatus(DepositCallbackRequest depositCallbackRequest) {
        Payment payment = findByOrderIdWithReservation(depositCallbackRequest.getOrderId());
        String status = depositCallbackRequest.getStatus();
        ReservationRecord reservationRecord = payment.getReservationRecord();
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
        payment.updateStatus(PaymentStatus.valueOf(status));
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

    private Payment findByOrderIdWithReservation(String orderId) {
        return paymentRepository.findByOrderIdWithReservation(orderId).orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));
    }

    private Payment findByOrderIdWithReservationAndPlace(String orderId) {
        return paymentRepository.findByOrderIdWithReservationAndPlace(orderId).orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));
    }



}
