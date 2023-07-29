package nerds.studiousTestProject.payment.dto.confirm.response.fromtoss;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor

public class Transfer {
    private String bankCode;
    private String settlementStatus;
}
