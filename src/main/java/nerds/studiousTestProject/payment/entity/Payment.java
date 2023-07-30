package nerds.studiousTestProject.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Payment {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "payment_state")
    private Boolean paymentState;

    @Column(name = "discount_price")
    private Integer discountPrice;

    @Column(name = "pay_complete_time")
    private LocalDateTime payCompleteTime;
}
