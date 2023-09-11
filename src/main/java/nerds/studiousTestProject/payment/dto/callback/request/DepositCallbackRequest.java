package nerds.studiousTestProject.payment.dto.callback.request;

import lombok.Getter;

@Getter
public class DepositCallbackRequest {

    private String createdAt;
    private String secret;
    private String status;
    private String transactionKey;
    private String orderId;

}
