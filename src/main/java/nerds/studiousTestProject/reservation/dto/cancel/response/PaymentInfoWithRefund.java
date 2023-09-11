package nerds.studiousTestProject.reservation.dto.cancel.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class PaymentInfoWithRefund {
    private String paymentMethod;
    private int price;
    private int refundPrice;
    private int refundFee;

}
