package nerds.studiousTestProject.member.exception.model;

import nerds.studiousTestProject.member.exception.message.ExceptionMessage;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String message) {
        super(message);
    }

    public TokenNotFoundException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage.message());
    }
}