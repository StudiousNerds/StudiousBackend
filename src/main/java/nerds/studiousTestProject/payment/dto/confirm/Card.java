package nerds.studiousTestProject.payment.dto.confirm;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Card {
    private int amount;
    private String issuerCode;
    @Nullable
    private String acquirerCode;
    private String number;
    private Integer installmentPlanMonths;
    private String approveNo;
    private boolean userCardPoint;
    private String cardType;
    private String ownerType;
    private String acquireStatus;
    private boolean isInterestFree;
    @Nullable
    private String interestPayer;
}
