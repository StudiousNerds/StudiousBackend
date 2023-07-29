package nerds.studiousTestProject.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment {

    @Id
    @GeneratedValue
    private Long id;
    private String orderId;
    private String paymentKey;
    private String type;
    private String completeTime;

    @Builder
    public Payment(Long id, String orderId, String paymentKey, String type, String completeTime) {
        this.id = id;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.type = type;
        this.completeTime = completeTime;
    }
}
