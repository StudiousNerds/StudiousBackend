package nerds.studiousTestProject.payment.dto;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.payment.entity.Payment;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse {

    private int price;
    private String method;
    private LocalDateTime completeTime;

    public static PaymentResponse from(Payment payment) {
        return PaymentResponse.builder()
                .price(payment.getPrice())
                .method(payment.getMethod())
                .completeTime(payment.getCompleteTime())
                .build();
    }

}
