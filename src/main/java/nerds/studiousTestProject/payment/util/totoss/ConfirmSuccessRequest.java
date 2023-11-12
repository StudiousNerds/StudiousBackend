package nerds.studiousTestProject.payment.util.totoss;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "orderId는 필수입니다.")
    private String orderId;

    @NotBlank(message = "paymentKey는 필수입니다.")
    private String paymentKey;

    @NotNull(message = "amount는 필수입니다.")
    private Integer amount;

}
