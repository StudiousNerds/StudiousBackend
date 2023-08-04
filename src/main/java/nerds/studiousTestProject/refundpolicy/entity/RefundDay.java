package nerds.studiousTestProject.refundpolicy.entity;

public enum RefundDay {

    BEFORE8("이용 8일 전"),
    BEFORE7("이용 7일 전"),
    BEFORE6("이용 6일 전"),
    BEFORE5("이용 5일 전"),
    BEFORE4("이용 4일 전"),
    BEFORE3("이용 3일 전"),
    BEFORE2("이용 2일 전"),
    BEFORE1("이용 1일 전"),
    NOW("이용 당일");

    private final String message;

    RefundDay(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
