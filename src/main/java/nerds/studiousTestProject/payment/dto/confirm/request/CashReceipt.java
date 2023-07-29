package nerds.studiousTestProject.payment.dto.confirm.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor

public class CashReceipt {
    private String type;
    private String receiptKey;
    private String issueNumber;
    private String receiptUrl;
    private int amount;
    private int taxFreeAmount;
}
