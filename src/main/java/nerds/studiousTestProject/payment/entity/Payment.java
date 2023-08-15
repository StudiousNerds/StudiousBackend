package nerds.studiousTestProject.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.payment.util.fromtoss.PaymentResponseFromToss;
import java.time.LocalDateTime;

import static nerds.studiousTestProject.common.exception.ErrorCode.PAYMENT_NOT_CANCELED;
import static nerds.studiousTestProject.payment.entity.PaymentStatus.*;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue
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

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Builder
    public Payment(Long id, String orderId, String paymentKey, String method, LocalDateTime completeTime, Integer price, PaymentStatus status, String cancelReason) {
        this.id = id;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.method = method;
        this.completeTime = completeTime;
        this.price = price;
        this.status = status;
        this.cancelReason = cancelReason;
    }

    public void canceled(PaymentResponseFromToss responseFromToss) {
        String cancelReason = responseFromToss.getCancels().get(0).getCancelReason();
        if (cancelReason != null) this.cancelReason = cancelReason;
        if (!status.equals(CANCELED.name())) throw new BadRequestException(PAYMENT_NOT_CANCELED);
        this.status = CANCELED;
    }
}
