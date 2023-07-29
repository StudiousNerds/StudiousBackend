package nerds.studiousTestProject.exception.notfound;

import nerds.studiousTestProject.exception.CustomException;
import nerds.studiousTestProject.exception.ErrorCode;

public class NotFoundException extends CustomException {
    public NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
