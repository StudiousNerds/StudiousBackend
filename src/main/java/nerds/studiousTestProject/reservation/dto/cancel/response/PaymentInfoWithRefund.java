package nerds.studiousTestProject.reservation.dto.cancel.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.reservation.dto.PaymentInfo;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class PaymentInfoWithRefund {

    private int totalPrice;
    private int refundPrice;
    private int refundFee;
    List<PaymentInfo> payment;

}
