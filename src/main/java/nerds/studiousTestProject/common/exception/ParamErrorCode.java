package nerds.studiousTestProject.common.exception;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ParamErrorCode {
    NOT_FOUND_PAGE("page", "페이지가 없습니다."),
    INVALID_KEYWORD("keyword", "키워드를 2글자 이상 입력해주세요."),
    INVALID_DATE("date", "날짜 입력이 잘못되었습니다."),
    INVALID_START_TIME("startTime", "이용 시간이 잘못되었습니다."),
    INVALID_END_TIME("end", "이용 시간이 잘못되었습니다."),
    INVALID_HEAD_COUNT("headCount", "인원 수는 최소 1명 이상이여야 합니다."),
    INVALID_MIN_GRADE("minGrade", "최소 평점은 0이상 5이하여야 합니다."),
    INVALID_MAX_GRADE("maxGrade", "최대 평점은 0이상 5이하여야 합니다."),
    INVALID_SORT_TYPE("sortType", "정렬 기준이 잘못되었습니다."),
    INVALID_HASHTAG_NAME("hashtags", "해시테그 이름이 잘못되었습니다."),
    INVALID_CONVENIENCE_NAME("conveniences", "편의시설 이름이 잘못되었습니다"),
    INVALID_CAFE_ID("cafeId", "카페 아이디 값이 잘못되었습니다.");

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
