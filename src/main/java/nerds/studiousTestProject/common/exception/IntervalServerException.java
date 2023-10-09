package nerds.studiousTestProject.common.exception;

import nerds.studiousTestProject.common.exception.errorcode.ErrorCode;

public class IntervalServerException extends CustomException {
    public IntervalServerException(ErrorCode errorCode) {
        super(errorCode);
    }
}
