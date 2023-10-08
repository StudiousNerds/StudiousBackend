package nerds.studiousTestProject.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.payment.entity.Payment;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentInfo {

    private int price;
    private String method;
    private LocalDateTime completeTime;

    public static PaymentInfo from(Payment payment) {
        return PaymentInfo.builder()
                .price(payment.getPrice())
                .method(payment.getMethod())
                .completeTime(payment.getCompleteTime())
                .build();
    }

}
