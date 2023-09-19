package nerds.studiousTestProject.common.exception;

import nerds.studiousTestProject.common.exception.errorcode.ErrorCode;

public class NotAuthorizedException extends CustomException {
    public NotAuthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
