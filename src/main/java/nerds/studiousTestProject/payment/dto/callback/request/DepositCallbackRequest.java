package nerds.studiousTestProject.payment.dto.callback.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DepositCallbackRequest {

    private String createdAt;
    private String secret;
    private String status;
    private String transactionKey;
    private String orderId;

}
