package nerds.studiousTestProject.payment.dto.confirm;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.exception.InvalidRequestToTossException;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmSuccessRequest {

    @NotNull(message = "orderId가 없습니다.")
    private String orderId;
    @NotNull(message = "amount가 없습니다.")
    private Integer amount;
    @NotNull(message = "paymentKey가 없습니다.")
    private String paymentKey;

}
