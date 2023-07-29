package nerds.studiousTestProject.payment.dto.confirm.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor

public class EasyPay {
    private String provider;
    private int amount;
    private int discountAmount;
}
