package nerds.studiousTestProject.payment.util.totoss;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancelRequest extends RequestToToss {
    private String cancelReason;
    private Integer cancelAmount;
}
