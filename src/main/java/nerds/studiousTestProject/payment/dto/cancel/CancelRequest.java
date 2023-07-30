package nerds.studiousTestProject.payment.dto.cancel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.payment.dto.RequestToToss;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancelRequest extends RequestToToss {
    private String cancelReason;
    private Integer cancelAmount;
}
