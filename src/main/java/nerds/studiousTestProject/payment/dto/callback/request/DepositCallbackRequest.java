package nerds.studiousTestProject.payment.dto.callback.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepositCallbackRequest {

    private String createdAt;
    private String secret;
    private String status;
    private String transactionKey;
    private String orderId;

}
