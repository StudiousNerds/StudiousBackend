package nerds.studiousTestProject.payment.dto.confirm;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Cancels {
    private int cancelAmount;
    private String cancelReason;
    private int taxFreeAmount;
    private Integer taxExemptionAmount;
    private int refundableAmount;
    private int easyPayDiscountAmount;
    private String canceledAt;
    private String transactionKey;
    private String receiptKey;
}
