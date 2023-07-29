package nerds.studiousTestProject.common.exception;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExceptionResponse {
    private String message;
    private String errorCode;

    public static ExceptionResponse from(String message, String code) {
        return ExceptionResponse.builder()
                .message(message)
                .errorCode(code)
                .build();
    }

    public static ExceptionResponse from(CustomException e) {
        return ExceptionResponse.builder()
                .message(e.getMessage())
                .errorCode(e.getErrorCode().name())
                .build();
    }

    @Builder
    public ExceptionResponse(String message, String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }
}
