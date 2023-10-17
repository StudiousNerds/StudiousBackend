package nerds.studiousTestProject.reservation.dto.cancel.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.payment.dto.PaymentInfo;
import nerds.studiousTestProject.payment.entity.Payment;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class PaymentInfoWithRefund {

    private int totalPrice;
    private int refundPrice;
    private int refundFee;
    private PaymentInfo payment;

    public static PaymentInfoWithRefund of(int totalPrice, int refundPrice, int refundFee, Payment payment) {
        return PaymentInfoWithRefund.builder()
                .totalPrice(totalPrice)
                .refundPrice(refundPrice)
                .refundFee(refundFee)
                .payment(PaymentInfo.from(payment))
                .build();
    }

}
