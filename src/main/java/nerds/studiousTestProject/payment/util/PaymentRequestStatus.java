package nerds.studiousTestProject.payment.util;

public enum PaymentRequestStatus {

    CONFIRM("https://api.tosspayments.com/v1/payments/confirm"),
    INQUIRY("https://api.tosspayments.com/v1/payments/%s"),
    CANCEL("https://api.tosspayments.com/v1/payments/%s/cancel");

    private final String uriFormat;

    PaymentRequestStatus(String uri) {
        this.uriFormat = uri;
    }

    public String getUriFormat() {
        return uriFormat;
    }

    public String getUriFormat(String paymentKey) {
        return String.format(this.uriFormat, paymentKey);
    }
}
