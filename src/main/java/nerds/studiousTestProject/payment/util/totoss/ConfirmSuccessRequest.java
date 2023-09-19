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


    private String orderId;

    private String paymentKey;

    private Integer amount;

}
