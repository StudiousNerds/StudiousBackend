package nerds.studiousTestProject.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "payment_state")
    private Boolean paymentState;

    @Column(name = "discount_price")
    private Integer discountPrice;

    @Column(name = "pay_complete_time")
    private LocalDateTime payCompleteTime;

    @Builder
    public Payment(Long id, String paymentType, Boolean paymentState, Integer discountPrice, LocalDateTime payCompleteTime) {
        this.id = id;
        this.paymentType = paymentType;
        this.paymentState = paymentState;
        this.discountPrice = discountPrice;
        this.payCompleteTime = payCompleteTime;
    }
}
