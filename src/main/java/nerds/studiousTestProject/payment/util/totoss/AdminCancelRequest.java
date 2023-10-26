package nerds.studiousTestProject.payment.util.totoss;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminCancelRequest extends RequestToToss {

    @NotBlank(message = "예약 취소 사유는 필수입니다.")
    private String cancelReason;

}
