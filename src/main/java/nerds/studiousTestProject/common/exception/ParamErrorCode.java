package nerds.studiousTestProject.common.exception;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ParamErrorCode {
    NOT_EXIST_PAGE("page", "페이지가 없습니다."),
    NOT_EXIST_CAFE_ID("cafeId", "카페 id가 없습니다."),
    // 공지사항 조회에서 사용되는 값들
    INVALID_START_DATE("startDate", "시작 날짜가 잘못되었습니다."),
    INVALID_END_DATE("endDate", "마감 날짜가 잘못되었습니다.");

    private static final Map<String, String> ENUM_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(ParamErrorCode::getParamName, ParamErrorCode::name))
    );
    private final String paramName;
    private final String message;

    ParamErrorCode(String paramName, String message) {
        this.paramName = paramName;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getParamName() {
        return paramName;
    }

    public static ParamErrorCode of(final String paramName) {
        return ParamErrorCode.valueOf(ENUM_MAP.get(paramName));
    }
}
