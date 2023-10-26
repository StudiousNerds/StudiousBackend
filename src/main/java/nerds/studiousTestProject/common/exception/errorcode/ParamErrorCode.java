package nerds.studiousTestProject.common.exception.errorcode;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ParamErrorCode {
    INVALID_PAGE("page", "페이지 값이 잘못되었습니다."),
    INVALID_CAFE_ID("studycafeId", "카페 id가 잘못되었습니다."),
    INVALID_DATE("date", "날짜가 잘못되었습니다."),
    // 공지사항 조회에서 사용되는 값들
    INVALID_START_DATE("startDate", "시작 날짜가 잘못되었습니다."),
    INVALID_END_DATE("endDate", "마감 날짜가 잘못되었습니다."),
    INVALID_START_TIME("starTime", "시작 시간이 잘못되었습니다."),
    INVALID_END_TIME("endTime", "마감 시간이 잘못되었습니다."),
    INVALID_HASH_TAGS("hashtags", "해시 테그가 잘못되었습니다."),
    INVALID_CONVENIENCES("conveniences", "편의 시설이 잘못되었습니다."),
    INVALID_SORT_TYPE("sortType", "정렬 기준이 잘못되었습니다."),
    INVALID_REVIEW_TYPE("reviewType", "리뷰 필터 조건이 잘못되었습니다.");

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
