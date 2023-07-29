package nerds.studiousTestProject.exception;

import static nerds.studiousTestProject.exception.ErrorCode.PAYMENT_NOT_FOUNT;

public class PaymentNotFoundException extends NotFoundException {

    public PaymentNotFoundException() {
        super(PAYMENT_NOT_FOUNT, "결제내역을 찾을 수 없습니다.");
    }
}
