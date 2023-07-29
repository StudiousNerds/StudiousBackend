package nerds.studiousTestProject.member.exception.model;

import nerds.studiousTestProject.member.exception.message.ExceptionMessage;

public class UserAuthException extends RuntimeException {
    public UserAuthException(String message) {
        super(message);
    }

    public UserAuthException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage.message());
    }
}
