package nerds.studiousTestProject.payment.util.totoss;

import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.errorcode.ErrorCode;

public class RefundReceiveAccount {
    private String bank;
    private String accountNumber;
    private String holderName;

    public void validRefundVirtualAccountPay(){
        if (bank == null || accountNumber == null || holderName == null) {
            throw new BadRequestException(ErrorCode.INVALID_REFUND_RECEIVE_ACCOUNT_INFO);
        }
    }
}
