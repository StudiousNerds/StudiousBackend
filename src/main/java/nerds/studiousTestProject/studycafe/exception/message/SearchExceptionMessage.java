package nerds.studiousTestProject.studycafe.exception.message;

/**
 * 서비스 레이어에서 발생하는 예외(SearchException) 관련 메시지
 */
public enum SearchExceptionMessage {
    NOT_SELECT_DATE("날짜 선택을 해주세요."),
    START_TIME_AFTER_THAN_END_TIME("시작 시간은 끝 시간보다 클 수 없습니다.");

    private final String message;
    SearchExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
