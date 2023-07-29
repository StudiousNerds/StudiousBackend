package nerds.studiousTestProject.payment.dto.confirm.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.exception.badrequest.InvalidRequestToTossException;
import nerds.studiousTestProject.payment.dto.RequestToToss;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmSuccessRequest extends RequestToToss {

    @NotNull(message = "orderId가 없습니다.")
    private String orderId;
    @NotNull(message = "amount가 없습니다.")
    private Integer amount;
    @NotNull(message = "paymentKey가 없습니다.")
    private String paymentKey;

    public static ConfirmSuccessRequest of(String orderId, Integer amount, String paymentKey) {
        if ((orderId == null) || (amount == null) || (paymentKey == null)) {
            throw new InvalidRequestToTossException();
        }
        return ConfirmSuccessRequest.builder()
                .orderId(orderId)
                .paymentKey(paymentKey)
                .amount(amount)
                .build();
    }


}
