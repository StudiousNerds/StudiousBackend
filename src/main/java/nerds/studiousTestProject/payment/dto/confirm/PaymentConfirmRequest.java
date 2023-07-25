package nerds.studiousTestProject.payment.dto.confirm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmRequest {
    private String orderId;
    private int amount;
    private String paymentKey;

}
