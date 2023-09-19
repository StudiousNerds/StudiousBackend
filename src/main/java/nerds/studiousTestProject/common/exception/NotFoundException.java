package nerds.studiousTestProject.common.exception;

import nerds.studiousTestProject.common.exception.errorcode.ErrorCode;

public class NotFoundException extends CustomException {
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}