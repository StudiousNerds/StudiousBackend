package nerds.studiousTestProject.common.exception;

public enum ErrorCode {

    INVALID_REQUEST_BODY_TYPE("유효하지 않은 요청입니다."),

    NOT_FOUND_STUDYCAFE("스터디카페를 찾을 수 없습니다."),

    NOT_FOUND_ROOM("스터디룸을 찾을 수 없습니다."),

    NOT_FOUND_HASHTAG("해시태그를 찾을 수 없습니다."),

    NOT_FOUND_MEMBER("멤버를 찾을 수 없습니다.");


    private final String message;

    ErrorCode(String code) {
        this.message = code;
    }

    public String getMessage() {
        return message;
    }
}
