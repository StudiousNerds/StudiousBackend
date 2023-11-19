package nerds.studiousTestProject.refundpolicy.entity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Remaining {

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
            Stream.of(values()).collect(Collectors.toMap(Remaining::getRemain, Remaining::name))
    );
    private final String message;
    private final int remain;

    Remaining(String message, int remain) {
        this.message = message;
        this.remain = remain;
    }

    public static Remaining of(int remain) {
        return Remaining.valueOf(ENUM_MAP.get(remain));
    }

    public static Remaining from(LocalDate reservationDate) {
        long remain = ChronoUnit.DAYS.between(LocalDate.now(), reservationDate);
        return Arrays.stream(Remaining.values())
                .filter(remaining -> remaining.remain == remain)
                .findAny()
                .orElse(BEFORE8);
    }

    public String getMessage() {
        return message;
    }
    public int getRemain() {
        return remain;
    }
}
