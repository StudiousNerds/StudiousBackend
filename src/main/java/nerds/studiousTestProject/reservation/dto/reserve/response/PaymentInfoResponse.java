package nerds.studiousTestProject.reservation.dto.reserve.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class PaymentInfoResponse {

    private int amount;

    private String orderId;

    private String orderName;

    public static PaymentInfoResponse of(Payment payment, String orderName) {
        return PaymentInfoResponse.builder()
                .amount(payment.getPrice())
                .orderId(payment.getOrderId())
                .orderName(orderName)
                .build();
    }

}



