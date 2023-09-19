package nerds.studiousTestProject.common.exception;

import nerds.studiousTestProject.common.exception.errorcode.ErrorCode;

public class BadRequestException extends CustomException {
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
