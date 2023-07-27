package nerds.studiousTestProject.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
public class Payment {

    @Id
    @GeneratedValue
    private Long id;

    private String orderId;
    private String paymentKey;
    private String type;
    private String completeTime;

}
