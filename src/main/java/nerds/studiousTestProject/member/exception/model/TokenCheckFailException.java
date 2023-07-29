package nerds.studiousTestProject.member.exception.model;

import nerds.studiousTestProject.member.exception.message.ExceptionMessage;

public class TokenCheckFailException extends RuntimeException {
    public TokenCheckFailException(String message) {
        super(message);
    }

    public TokenCheckFailException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage.message());
    }
}