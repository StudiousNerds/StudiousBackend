package nerds.studiousTestProject.refundpolicy.entity;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private static final Map<Integer, String> ENUM_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(RefundDay::getRemain, RefundDay::name))
    );
    private final String message;
    private final int remain;

    RefundDay(String message, int remain) {
        this.message = message;
        this.remain = remain;
    }

    public static RefundDay of(int remain) {
        return RefundDay.valueOf(ENUM_MAP.get(remain));
    }

    public String getMessage() {
        return message;
    }
    public int getRemain() {
        return remain;
    }
}
