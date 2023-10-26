package nerds.studiousTestProject.payment.util.fromtoss;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.payment.entity.BankCode;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.payment.entity.PaymentStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class PaymentResponseFromToss {

    private String version;
    private String paymentKey;
    private String type;
    private String orderId;
    private String orderName;
    private String mId;
    private String currency;
    private String method;
    private int totalAmount;
    private Integer balanceAmount;
    private String status;
    private String requestedAt;
    private String approvedAt;
    private boolean useEscrow;
    @Nullable
    private String lastTransactionKey;
    private int vat;
    private boolean cultureExpense;
    private int taxFreeAmount;
    private Integer taxExemptionAmount;
    @Nullable
    private List<Cancel> cancels;
    private boolean isPartialCancelable;
    @Nullable
    private Card card;
    @Nullable
    private VirtualAccount virtualAccount;
    @Nullable
    private String secret;
    @Nullable
    private MobilePhone mobilePhone;
    @Nullable
    private GiftCertificate giftCertificate;
    @Nullable
    private Transfer transfer;
    @Nullable
    private Receipt receipt;
    @Nullable
    private EasyPay easyPay;
    private String country;
    @Nullable
    private Failure failure;
    @Nullable
    private CashReceipt cashReceipt;
    @Nullable
    private List<CashReceipts> cashReceipts;
    @Nullable
    private Discount discount;

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
    public Payment toPayment(){
        return Payment.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .price(totalAmount)
                .completeTime(LocalDateTime.parse(approvedAt, DateTimeFormatter.ofPattern(DATE_FORMAT)))
                .method(method)
                .status(PaymentStatus.valueOf(status))
                .build();
    }

    public Payment toVitualAccountPayment(){
        return Payment.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .price(totalAmount)
                .method(method)
                .status(PaymentStatus.valueOf(status))
                .dueDate(LocalDateTime.parse(virtualAccount.getDueDate(), DateTimeFormatter.ofPattern(DATE_FORMAT)))
                .bankCode(BankCode.from(virtualAccount.getBankCode()))
                .virtualAccount(virtualAccount.getAccountNumber())
                .secret(secret)
                .build();
    }
}
