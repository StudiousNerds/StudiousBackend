package nerds.studiousTestProject.reservation.dto.cancel.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.payment.dto.PaymentResponse;
import nerds.studiousTestProject.payment.entity.Payment;

@AllArgsConstructor
@Getter
@Builder
public class PaymentWithRefundResponse {

    private int refundPrice;
    private int refundFee;
    private PaymentResponse payment;

    public static PaymentWithRefundResponse of(int refundPrice, int refundFee, Payment payment) {
        return PaymentWithRefundResponse.builder()
                .refundPrice(refundPrice)
                .refundFee(refundFee)
                .payment(PaymentResponse.from(payment))
                .build();
    }

}
