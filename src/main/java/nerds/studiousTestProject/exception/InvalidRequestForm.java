package nerds.studiousTestProject.exception;

public class InvalidRequestForm extends CustomException{
    public InvalidRequestForm(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
