package nerds.studiousTestProject.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.member.entity.member.MemberRole;
import nerds.studiousTestProject.payment.util.fromtoss.PaymentResponseFromToss;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static nerds.studiousTestProject.common.exception.ErrorCode.PAYMENT_NOT_CANCELED;
import static nerds.studiousTestProject.payment.entity.PaymentStatus.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, updatable = false)
    private String orderId;

    @Column(name = "payment_key", nullable = false, updatable = false)
    private String paymentKey;

    @Column(name = "method", nullable = false)
    private String method;

    @Column(name = "complete_time", nullable = false)
    private LocalDateTime completeTime;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "virtual_account")
    private String virtualAccount;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "secret")
    private String secret;

    @Column(name = "canceler")
    private MemberRole canceler;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_record_id", nullable = false)
    private ReservationRecord reservationRecord;

    @Builder
    public Payment(Long id, String orderId, String paymentKey, String method, LocalDateTime completeTime, Integer price, PaymentStatus status, String cancelReason, String virtualAccount, String bankCode, LocalDateTime dueDate, String secret, MemberRole canceler, ReservationRecord reservationRecord) {
        this.id = id;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.method = method;
        this.completeTime = completeTime;
        this.price = price;
        this.status = status;
        this.cancelReason = cancelReason;
        this.virtualAccount = virtualAccount;
        this.bankCode = bankCode;
        this.dueDate = dueDate;
        this.secret = secret;
        this.canceler = canceler;
        this.reservationRecord = reservationRecord;
    }


    public void cancel(PaymentResponseFromToss responseFromToss) {
        String cancelReason = responseFromToss.getCancels().get(0).getCancelReason();
        if (cancelReason != null) this.cancelReason = cancelReason;
        if (!status.equals(CANCELED.name())) throw new BadRequestException(PAYMENT_NOT_CANCELED);
        this.status = CANCELED;
    }

    public void updateStatus(PaymentStatus paymentStatus) {
        if (paymentStatus != null) {
            this.status = paymentStatus;
        }
    }

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";

    public void updateCompleteTime(String completeTime) {
        if (completeTime != null) {
            this.completeTime = LocalDateTime.parse(completeTime, DateTimeFormatter.ofPattern(DATE_FORMAT));
        }
    }

    private void updatePaymentKey(String paymentKey) {
        if (paymentKey != null) {
            this.paymentKey = paymentKey;
        }
    }

    private void updatePrice(Integer price) {
        if (price != null) {
            this.price = price;
        }
    }

    private void updateMethod(String method) {
        if (method != null) {
            this.method = method;
        }
    }

    private void updateOrderId(String orderId) {
        if (orderId != null) {
            this.orderId = orderId;
        }
    }

    private void updateCompleteTime(LocalDateTime completeTime) {
        if (completeTime != null) {
            this.completeTime = completeTime;
        }
    }

    private void updateDueDate(LocalDateTime dueDate) {
        if (dueDate != null) {
            this.dueDate = dueDate;
        }
    }

    private void updateBankCode(String bankCode) {
        if (bankCode != null) {
            this.bankCode = bankCode;
        }
    }

    private void updateVirtualAccount(String virtualAccount) {
        if (virtualAccount != null) {
            this.virtualAccount = virtualAccount;
        }
    }

    private void updateSecret(String secret) {
        if (secret != null) {
            this.secret = secret;
        }
    }


    public void complete(Payment payment) {
        updateStatus(payment.getStatus());
        updatePaymentKey(payment.getPaymentKey());
        updatePrice(payment.getPrice());
        updateOrderId(payment.getOrderId());
        updateCompleteTime(payment.getCompleteTime());
        updateMethod(payment.getMethod());
        updateDueDate(payment.getDueDate());
        updateBankCode(payment.getBankCode());
        updateSecret(payment.getSecret());
        updateVirtualAccount(payment.getVirtualAccount());
    }
}
