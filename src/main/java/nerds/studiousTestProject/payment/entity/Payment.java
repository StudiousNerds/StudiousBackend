package nerds.studiousTestProject.payment.entity;

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

import static nerds.studiousTestProject.common.exception.ErrorCode.PAYMENT_NOT_CANCELED;
import static nerds.studiousTestProject.payment.entity.PaymentStatus.*;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue
    private Long id;
    private String orderId;
    private String paymentKey;
    private String method;
    private String completeTime;
    private Integer price;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private String cancelReason;

    @Builder
    public Payment(Long id, String orderId, String paymentKey, String method, String completeTime, Integer price, PaymentStatus paymentStatus, String cancelReason) {
        this.id = id;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.method = method;
        this.completeTime = completeTime;
        this.price = price;
        this.paymentStatus = paymentStatus;
        this.cancelReason = cancelReason;
    }

    public void canceled(PaymentResponseFromToss responseFromToss) {
        String cancelReason = responseFromToss.getCancels().get(0).getCancelReason();
        if (cancelReason != null) this.cancelReason = cancelReason;
        if (!paymentStatus.equals(CANCELED.name())) throw new BadRequestException(PAYMENT_NOT_CANCELED);
        this.paymentStatus = CANCELED;
    }
}
