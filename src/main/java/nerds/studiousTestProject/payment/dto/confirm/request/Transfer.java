package nerds.studiousTestProject.payment.dto.confirm.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor

public class Transfer {
    private String bankCode;
    private String settlementStatus;
}