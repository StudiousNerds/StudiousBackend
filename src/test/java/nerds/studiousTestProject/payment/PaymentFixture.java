package nerds.studiousTestProject.payment;

import nerds.studiousTestProject.payment.util.fromtoss.PaymentResponseFromToss;
import nerds.studiousTestProject.payment.util.fromtoss.Cancels;

import java.util.List;

public class PaymentFixture {

    public static final String requestedAt = "2022-01-01T00:00:00+09:00";
    public static final String type = "NORMAL";
    public static final String orderId = "a4CWyWY5m89PNh7xJwhk1";
    public static final String paymentKey = "5zJ4xY7m0kODnyRpQWGrN2xqGlNvLrKwv1M9ENjbeoPaZdL6";

    public static int amount = 1000;
    public static String cancelReason = "고객 변심";
    public static int taxFreeAmount = 0;
    public static Integer taxExemptionAmount = 0;
    public static int refundableAmount = 0;
    public static int easyPayDiscountAmount = 0;
    public static String canceledAt = "2022-01-01T11:32:04+09:00";
    public static String transactionKey = "66630503F0C4BA6F33FF33EAC15249C2";
    public static String receiptKey = "Ik43Gc0hQldGN0l8iPRIbA0YMNdMRnPcZGIQnpLkzU6mhvE0";


    public static PaymentResponseFromToss createMockCancelResponse() {
        Cancels cancel = Cancels.builder()
                .canceledAt(canceledAt)
                .taxFreeAmount(taxFreeAmount)
                .cancelAmount(amount)
                .cancelReason(cancelReason)
                .easyPayDiscountAmount(easyPayDiscountAmount)
                .transactionKey(transactionKey)
                .receiptKey(receiptKey)
                .refundableAmount(refundableAmount)
                .taxExemptionAmount(taxExemptionAmount)
                .build();
        List<Cancels> cancels = List.of(cancel);
        return PaymentResponseFromToss.builder()
                .cancels(cancels)
                .build();
    }

    public static PaymentResponseFromToss createMockSuccessConfirmResponseFromToss(){
        return PaymentResponseFromToss.builder()
                .requestedAt(requestedAt)
                .type(type)
                .orderId(orderId)
                .paymentKey(paymentKey)
                .build();
    }


}
