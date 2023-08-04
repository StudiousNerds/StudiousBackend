package nerds.studiousTestProject.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


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

    @Builder
    public Payment(Long id, String orderId, String paymentKey, String method, String completeTime) {
        this.id = id;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.method = method;
        this.completeTime = completeTime;
    }
}
