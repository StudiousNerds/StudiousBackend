package nerds.studiousTestProject.payment.util.totoss;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancelRequest extends RequestToToss {

    @NotBlank(message = "예약 취소 사유는 필수입니다.")
    private String cancelReason;
    private Integer cancelAmount;
    private RefundReceiveAccount refundReceiveAccount;

}
