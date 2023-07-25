package nerds.studiousTestProject.payment.dto.confirm;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class PaymentConfirmResponseFromToss {
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
    private List<Cancels> cancels;
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
}
