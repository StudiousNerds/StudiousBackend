package nerds.studiousTestProject.payment.util.totoss;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.common.exception.ErrorCode;
import nerds.studiousTestProject.common.exception.BadRequestException;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmSuccessRequest extends RequestToToss {

    private String orderId;
    private Integer amount;
    private String paymentKey;

    public static ConfirmSuccessRequest of(String orderId, Integer amount, String paymentKey) {
        if ((orderId == null) || (amount == null) || (paymentKey == null)) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST_BODY_TYPE);
        }
        return ConfirmSuccessRequest.builder()
                .orderId(orderId)
                .paymentKey(paymentKey)
                .amount(amount)
                .build();
    }


}
