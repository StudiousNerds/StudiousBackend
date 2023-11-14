package nerds.studiousTestProject.reservation.dto.reserve.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.payment.entity.Payment;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class PaymentResponse {

    private int amount;

    private String orderId;

    private String orderName;

    public static PaymentResponse of(Payment payment, String orderName) {
        return PaymentResponse.builder()
                .amount(payment.getPrice())
                .orderId(payment.getOrderId())
                .orderName(orderName)
                .build();
    }

}



