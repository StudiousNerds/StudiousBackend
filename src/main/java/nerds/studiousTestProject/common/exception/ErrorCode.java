package nerds.studiousTestProject.common.exception;

public enum ErrorCode {

    INVALID_REQUEST_BODY_TYPE("입력 값이 잘못 되었습니다."),
    NOT_FOUND_ROOM("스터디룸을 찾을 수 없습니다."),
    NOT_FOUND_RESERVATION_RECORD("예약내역을 찾을 수 없습니다."),
    NOT_FOUND_PAYMENT("결제 내역을 찾을 수 없습니다."),
    INVALID_REQUEST_BODY_TYPE_TO_TOSS("토스에게 요청하는 값은 null 값이 있어선 안됩니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
