package nerds.studiousTestProject.payment.dto.confirm;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor

public class MobilePhone {
    private String customerMobilePhone;
    private String settlementStatus;
    private String receiptUrl;
}
