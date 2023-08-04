package nerds.studiousTestProject.refundpolicy.entity;

public enum RefundDay {

    BEFORE8("이용 8일 전", 8),
    BEFORE7("이용 7일 전", 7),
    BEFORE6("이용 6일 전", 6),
    BEFORE5("이용 5일 전", 5),
    BEFORE4("이용 4일 전", 4),
    BEFORE3("이용 3일 전", 3),
    BEFORE2("이용 2일 전", 2),
    BEFORE1("이용 1일 전", 1),
    NOW("이용 당일", 0);

    private final String message;
    private final int remain;

    RefundDay(String message, int remain) {
        this.message = message;
        this.remain = remain;
    }

    public String getMessage() {
        return message;
    }
}
