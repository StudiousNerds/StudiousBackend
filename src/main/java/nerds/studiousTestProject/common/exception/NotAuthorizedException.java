package nerds.studiousTestProject.common.exception;

public class NotAuthorizedException extends CustomException {
    public NotAuthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
