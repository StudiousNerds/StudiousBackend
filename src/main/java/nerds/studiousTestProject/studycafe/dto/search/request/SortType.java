package nerds.studiousTestProject.studycafe.dto.search.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum SortType {
    GRADE_DESC,
    RESERVATION_DESC,
    REVIEW_DESC,
    REVIEW_ASC,
    CREATED_DESC;

    /**
     * Controller 레이어에서 JSON Parser 동작 시 열거체 매핑을 해주는 메소드 (이상 한 값 필터 후 기본값으로 설정)
     * @param param
     * @return
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static SortType get(String param) {
        return Arrays.stream(values())
                .filter(s -> s.name().equals(param))
                .findAny()
                .orElse(SortType.GRADE_DESC);
    }
}
