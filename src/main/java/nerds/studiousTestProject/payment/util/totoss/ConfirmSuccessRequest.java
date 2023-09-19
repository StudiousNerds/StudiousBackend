package nerds.studiousTestProject.payment.util.totoss;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class ConfirmSuccessRequest extends RequestToToss {

    @NotNull
    private String orderId;
    @NotNull
    private Integer amount;
    @NotNull
    private String paymentKey;

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setPaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }
}
