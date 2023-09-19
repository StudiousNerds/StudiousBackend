package nerds.studiousTestProject.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.member.entity.member.MemberRole;
import nerds.studiousTestProject.payment.util.fromtoss.PaymentResponseFromToss;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.PAYMENT_NOT_CANCELED;
import static nerds.studiousTestProject.payment.entity.PaymentStatus.*;


@Getter
@Entity
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

    @Column(name = "payment_status")
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

    @Builder
    public Payment(Long id, String orderId, String paymentKey, String method, LocalDateTime completeTime, Integer price, PaymentStatus status, String cancelReason, String virtualAccount, String bankCode, LocalDateTime dueDate, String secret) {
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
    }

    public void canceled(PaymentResponseFromToss responseFromToss) {
        String cancelReason = responseFromToss.getCancels().get(0).getCancelReason();
        if (cancelReason != null) this.cancelReason = cancelReason;
        if (!status.equals(CANCELED.name())) throw new BadRequestException(PAYMENT_NOT_CANCELED);
        this.status = CANCELED;
    }

    public void updateStatus(String paymentStatus) {
        if (paymentStatus != null) {
            this.status = PaymentStatus.valueOf(paymentStatus);
        }
    }

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";

    public void updateCompleteTime(String completeTime) {
        if (completeTime != null) {
            this.completeTime = LocalDateTime.parse(completeTime, DateTimeFormatter.ofPattern(DATE_FORMAT));
        }
    }
}
