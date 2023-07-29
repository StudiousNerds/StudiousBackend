package nerds.studiousTestProject.exception;

public class InvalidRequestToTossException extends InvalidRequestForm {

    public InvalidRequestToTossException() {
        super(ErrorCode.INVALID_REQUEST_BODY_TYPE, "토스에게 요청하는 값은 null 값이 있어선 안됩니다.");
    }
}
