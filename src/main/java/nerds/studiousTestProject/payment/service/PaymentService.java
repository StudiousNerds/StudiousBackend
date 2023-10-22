package nerds.studiousTestProject.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.convenience.repository.ConvenienceRecordRepository;
import nerds.studiousTestProject.member.entity.member.MemberRole;
import nerds.studiousTestProject.payment.dto.callback.request.DepositCallbackRequest;
import nerds.studiousTestProject.payment.dto.virtual.response.VirtualAccountInfoResponse;
import nerds.studiousTestProject.payment.entity.PaymentStatus;
import nerds.studiousTestProject.payment.util.fromtoss.Cancel;
import nerds.studiousTestProject.payment.util.totoss.AdminCancelRequest;
import nerds.studiousTestProject.payment.util.totoss.ConfirmSuccessRequest;
import nerds.studiousTestProject.payment.util.fromtoss.PaymentResponseFromToss;
import nerds.studiousTestProject.payment.util.totoss.CancelRequest;
import nerds.studiousTestProject.payment.dto.confirm.response.ConfirmFailResponse;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.payment.repository.PaymentRepository;
import nerds.studiousTestProject.payment.util.PaymentGenerator;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import static nerds.studiousTestProject.payment.util.PaymentRequestStatus.CANCEL;
import static nerds.studiousTestProject.payment.util.PaymentRequestStatus.CONFIRM;
import static nerds.studiousTestProject.payment.util.PaymentRequestStatus.INQUIRY;


@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentGenerator paymentGenerator;
    private final ReservationRecordRepository reservationRecordRepository;
    private final ConvenienceRecordRepository convenienceRecordRepository;

    @Transactional
    public Long confirmSuccess(final String orderId, final String paymentKey, final Integer amount) {
        Payment payment = findByOrderId(orderId);
        validConfirmRequest(orderId, amount, payment);
        final PaymentResponseFromToss responseFromToss = paymentGenerator.requestToToss(CONFIRM.getUriFormat(), new ConfirmSuccessRequest(orderId, paymentKey, amount));
        payment.complete(responseFromToss.toPayment());
        ReservationRecord reservationRecord = findReservationRecordByPaymentIdWithPlace(payment.getId());
        reservationRecord.completePay();
        reservationRecord.getRoom().getStudycafe().updateAccumReserveCount();
        return reservationRecord.getId();
    }

    private ReservationRecord findReservationRecordByPaymentIdWithPlace(Long paymentId) {
        return reservationRecordRepository.findByPaymentWithPlace(paymentId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));
    }

    private void validConfirmRequest(final String orderId, final Integer amount, final Payment payment){
        if(!amount.equals(payment.getPrice())) throw new BadRequestException(MISMATCH_PRICE);
        if(!orderId.equals(payment.getOrderId())) throw new BadRequestException(MISMATCH_ORDER_ID);
    }

    @Transactional
    public VirtualAccountInfoResponse virtualAccount(final String orderId, final String paymentKey, final Integer amount) {
        Payment payment = findByOrderId(orderId);
        validConfirmRequest(orderId, amount, payment);
        PaymentResponseFromToss responseFromToss = paymentGenerator.requestToToss(INQUIRY.getUriFormat(paymentKey));
        validPaymentMethod(responseFromToss);
        payment.complete(responseFromToss.toVitualAccountPayment()); //complete 말고 다른 작명이 좋을 수도 있을 듯
        log.info("success payment ! payment status is {} and method is {}", responseFromToss.getStatus(), responseFromToss.getMethod());
        return VirtualAccountInfoResponse.from(payment);
    }

    private void validPaymentMethod(final PaymentResponseFromToss responseFromToss) {
        if (!responseFromToss.getMethod().equals(VIRTUAL_ACCOUNT.getValue())) {
            throw new BadRequestException(MISMATCH_PAYMENT_METHOD);
        }
    }

    /**
     * 지금은 결제 실패시 무조건 예약 내역, 결제 삭제
     * 예약 변경 중 실패한다면 롤백할 필요가 있어보임
     * 변경 기록 중 reservation Id 가 동일한게 있다면 -> 최근 변경 기록 전으로 롤백하는 기능추가가 필요해 보임
     * @param message
     * @param orderId
     * @return
     */
    @Transactional
    public ConfirmFailResponse confirmFail(final String message, final String orderId){
        final Payment payment = findByOrderId(orderId);
        reservationRecordRepository.delete(findReservationRecordByPayment(payment));
        //convenienceRecordRepository.findAllByPayment(payment).stream().forEach(convenience -> convenienceRecordRepository.delete(convenience));
        paymentRepository.delete(payment);
        return ConfirmFailResponse.of(message);
    }

    private ReservationRecord findReservationRecordByPayment(Payment payment) {
        return reservationRecordRepository.findByPayment(payment).orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));
    }

    @Transactional
    public void userCancel(final CancelRequest cancelRequest, final Long reservationId){
        ReservationRecord reservationRecord = findReservationRecordByIdWithPayment(reservationId);
        Payment payment = reservationRecord.getPayment();
        //취소 금액 validation 해야 하는데, validate 하는 메서드가 reservationService에 있음 설계 엉망..
        cancel(cancelRequest, MemberRole.USER, reservationRecord, payment);
    }

    private void cancel(final CancelRequest cancelRequest, final MemberRole canceler, ReservationRecord reservationRecord, Payment payment) {
        validPaymentMethod(cancelRequest, payment);
        final PaymentResponseFromToss responseFromToss = paymentGenerator.requestToToss(CANCEL.getUriFormat(payment.getPaymentKey()), cancelRequest);
        validMatchCancelAmount(payment, responseFromToss);
        payment.cancel(responseFromToss, canceler);
        reservationRecord.canceled();
        reservationRecord.getRoom().getStudycafe().cancelReservation();
    }

    private void validMatchCancelAmount(Payment payment, PaymentResponseFromToss responseFromToss) {
        if (responseFromToss.getCancels().stream().mapToInt(Cancel::getCancelAmount).sum() != payment.getPrice()){
            throw new BadRequestException(MISMATCH_CANCEL_PRICE);
        }
    }

    @Transactional
    public void adminCancel(final AdminCancelRequest adminCancelRequest, final Long reservationId) {
        ReservationRecord reservationRecord = findReservationRecordByIdWithPayment(reservationId);
        Payment payment = reservationRecord.getPayment();
        CancelRequest cancelRequest = CancelRequest.builder()
                .cancelAmount(payment.getPrice())
                .cancelReason(adminCancelRequest.getCancelReason())
                .build();
        cancel(cancelRequest, MemberRole.ADMIN, reservationRecord, payment);
    }

    private ReservationRecord findReservationRecordByIdWithPayment(Long reservationId) {
        return reservationRecordRepository.findByIdWithPayment(reservationId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_RESERVATION_RECORD));
    }

    private void validPaymentMethod(final CancelRequest cancelRequest, final Payment payment) {
        if (payment.getMethod().equals(VIRTUAL_ACCOUNT.getValue())) {
            cancelRequest.getRefundReceiveAccount().validRefundVirtualAccountPay();
        }
    }

    public void processDepositByStatus(final DepositCallbackRequest depositCallbackRequest) {
        Payment payment = findByOrderId(depositCallbackRequest.getOrderId());
        ReservationRecord reservationRecord = findReservationRecordByPayment(payment);
        final String status = depositCallbackRequest.getStatus();
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

    private boolean isDepositError(final Payment payment, final String status) {
        return status.equals(WAITING_FOR_DEPOSIT.name()) && payment.getStatus().equals(DONE);
    }

    private void validPaymentSecret(final DepositCallbackRequest depositCallbackRequest, final Payment payment) {
        if (!payment.getSecret().equals(depositCallbackRequest.getSecret())) {
            throw new BadRequestException(INVALID_PAYMENT_SECRET);
        }
    }

    private Payment findByOrderId(final String orderId) {
        return paymentRepository.findByOrderId(orderId).orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));
    }
}
