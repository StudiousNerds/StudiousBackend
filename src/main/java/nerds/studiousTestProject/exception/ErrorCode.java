package nerds.studiousTestProject.exception;

public enum ErrorCode {

    INVALID_REQUEST_BODY_TYPE("400"),

    NOT_NULL("400"),
    ROOM_NOT_FOUND("400"),
    RESERVATION_RECORD_NOT_FOUND("400"),
    PAYMENT_NOT_FOUNT("400");


    private String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
