package nerds.studiousTestProject.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.member.entity.member.MemberRole;
import nerds.studiousTestProject.payment.dto.callback.request.DepositCallbackRequest;
import nerds.studiousTestProject.payment.dto.confirm.response.SuccessPayResponse;
import nerds.studiousTestProject.payment.dto.virtual.response.VirtualAccountResponse;
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
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
import nerds.studiousTestProject.refundpolicy.entity.Remaining;
import nerds.studiousTestProject.refundpolicy.repository.RefundPolicyRepository;
import nerds.studiousTestProject.reservation.dto.cancel.response.PaymentWithRefundResponse;
import nerds.studiousTestProject.reservation.dto.cancel.response.ReservationCancelResponse;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_RESERVATION_CANCEL_DATE;
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
    private final RefundPolicyRepository refundPolicyRepository;

    @Transactional
    public SuccessPayResponse confirmSuccess(ConfirmSuccessRequest request) {
        Payment payment = findByOrderId(request.getOrderId());
        validConfirmRequest(request, payment);
        final PaymentResponseFromToss responseFromToss = paymentGenerator.requestToToss(CONFIRM.getUriFormat(), request);
        payment.complete(responseFromToss.toPayment());
        ReservationRecord reservationRecord = findReservationRecordByPaymentIdWithPlace(payment.getId());
        reservationRecord.completePay();
        reservationRecord.getRoom().getStudycafe().updateAccumReserveCount();
        return new SuccessPayResponse(reservationRecord.getId());
    }

    private ReservationRecord findReservationRecordByPaymentIdWithPlace(Long paymentId) {
        return reservationRecordRepository.findByPaymentWithPlace(paymentId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));
    }

    private void validConfirmRequest(ConfirmSuccessRequest request, final Payment payment){
        if(!request.getAmount().equals(payment.getPrice())) throw new BadRequestException(MISMATCH_PRICE);
        if(!request.getOrderId().equals(payment.getOrderId())) throw new BadRequestException(MISMATCH_ORDER_ID);
    }

    @Transactional
    public VirtualAccountResponse virtualAccount(ConfirmSuccessRequest request) {
        Payment payment = findByOrderId(request.getOrderId());
        validConfirmRequest(request, payment);
        PaymentResponseFromToss responseFromToss = paymentGenerator.requestToToss(INQUIRY.getUriFormat(request.getPaymentKey()));
        validPaymentMethod(responseFromToss);
        payment.complete(responseFromToss.toVitualAccountPayment()); //complete 말고 다른 작명이 좋을 수도 있을 듯
        log.info("success payment ! payment status is {} and method is {}", responseFromToss.getStatus(), responseFromToss.getMethod());
        return VirtualAccountResponse.from(payment);
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
    public void userCancel(final CancelRequest cancelRequest, final Long reservationId) {
        ReservationRecord reservationRecord = findReservationRecordByIdWithPayment(reservationId);
        Payment payment = reservationRecord.getPayment();
        validCancelAmount(cancelRequest, reservationRecord, payment);
        cancel(cancelRequest, MemberRole.USER, reservationRecord, payment);
    }

    private void validCancelAmount(CancelRequest cancelRequest, ReservationRecord reservationRecord, Payment payment) {
        Studycafe studycafe = reservationRecord.getRoom().getStudycafe();
        int refundRate = refundPolicyRepository.findStudycafeRefundRateOnDay(studycafe, Remaining.from(reservationRecord.getDate()));
        double refundPrice = payment.getPrice() * refundRate * 0.01;
        if (refundPrice != cancelRequest.getCancelAmount()) {
            throw new BadRequestException(MISMATCH_CANCEL_PRICE);
        }
    }

    private void cancel(final CancelRequest cancelRequest, final MemberRole canceler, ReservationRecord reservationRecord, Payment payment) {
        validPaymentMethod(cancelRequest, payment);
        final PaymentResponseFromToss responseFromToss = paymentGenerator.requestToToss(CANCEL.getUriFormat(payment.getPaymentKey()), cancelRequest);
        validMatchCancelAmount(payment, responseFromToss);
        payment.cancel(responseFromToss, canceler);
        reservationRecord.canceled();
        reservationRecord.getRoom().getStudycafe().cancelReservation();
    }

    private void validMatchCancelAmount(final Payment payment, final PaymentResponseFromToss responseFromToss) {
        final int responseTotalCancelAmount = responseFromToss.getCancels().stream().mapToInt(Cancel::getCancelAmount).sum();
        if (responseTotalCancelAmount != payment.getPrice()){
            throw new BadRequestException(MISMATCH_CANCEL_PRICE);
        }
    }

    @Transactional
    public void adminCancel(final AdminCancelRequest adminCancelRequest, final Long reservationId) {
        final ReservationRecord reservationRecord = findReservationRecordByIdWithPayment(reservationId);
        final Payment payment = reservationRecord.getPayment();
        final CancelRequest cancelRequest = CancelRequest.builder()
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

    public ReservationCancelResponse getCancel(final Long reservationId) {
        final ReservationRecord reservationRecord = findByIdWithPaymentAndPlace(reservationId);
        final Studycafe studycafe = reservationRecord.getRoom().getStudycafe();
        final List<RefundPolicy> refundPolicies = refundPolicyRepository.findAllByStudycafe(studycafe);

        final int remainDate = getRemainDate(reservationRecord.getDate(), LocalDate.now());
        final RefundPolicy refundPolicyOnDay = getRefundPolicyOnDay(refundPolicies, remainDate);
        PaymentWithRefundResponse paymentWithRefundResponse = createPaymentWithRefundResponse(reservationRecord.getPayment(), refundPolicyOnDay);
        return ReservationCancelResponse.of(reservationRecord, paymentWithRefundResponse, refundPolicies, refundPolicyOnDay);
    }

    private ReservationRecord findByIdWithPaymentAndPlace(Long reservationRecordId) {
        return reservationRecordRepository.findByIdWithPaymentAndPlace(reservationRecordId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_RESERVATION_RECORD));
    }

    private PaymentWithRefundResponse createPaymentWithRefundResponse(final Payment payment, final RefundPolicy refundPolicyOnDay) {
        final int price = payment.getPrice();
        final int refundPrice = calculateRefundPrice(refundPolicyOnDay, price);
        final int refundFee = price - refundPrice;
        return PaymentWithRefundResponse.of(refundPrice, refundFee, payment);
    }

    private int calculateRefundPrice(RefundPolicy refundPolicyOnDay, int price) {
        return price * refundPolicyOnDay.getRate() * (1 / 100);
    }

    private RefundPolicy getRefundPolicyOnDay(final List<RefundPolicy> refundPolicies, final int remainDate) {
        return refundPolicies.stream()
                .filter(refundPolicy -> refundPolicy.getRemaining().getRemain() == remainDate)
                .findFirst()
                .orElseThrow(() -> new BadRequestException(INVALID_RESERVATION_CANCEL_DATE));
    }

    private int getRemainDate(final LocalDate reservationDate, final LocalDate now) {
        int remainDate = reservationDate.getDayOfYear() - now.getDayOfYear();
        return remainDate > 8 ? 8 : remainDate;
    }

}
