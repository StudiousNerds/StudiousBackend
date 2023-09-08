package nerds.studiousTestProject.reservation.dto.detail.response;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.payment.entity.Payment;

@Getter
@Builder
public class PaymentInfo {

    private int price;
    private String method;

    public static PaymentInfo from(Payment payment) {
        return PaymentInfo.builder()
                .price(payment.getPrice())
                .method(payment.getMethod())
                .build();
    }

}
