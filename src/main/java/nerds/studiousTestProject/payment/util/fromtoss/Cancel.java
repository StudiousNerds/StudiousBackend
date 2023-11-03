package nerds.studiousTestProject.payment.util.fromtoss;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Cancel {

    private Integer cancelAmount;
    private String cancelReason;
    private Integer taxFreeAmount;
    private Integer taxExemptionAmount;
    private Integer refundableAmount;
    private Integer easyPayDiscountAmount;
    private String canceledAt;
    private String transactionKey;
    private String receiptKey;
}
