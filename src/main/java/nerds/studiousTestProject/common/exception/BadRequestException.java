package nerds.studiousTestProject.common.exception;

public class BadRequestException extends CustomException{
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}