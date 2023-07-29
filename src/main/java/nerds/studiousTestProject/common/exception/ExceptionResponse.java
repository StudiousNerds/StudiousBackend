package nerds.studiousTestProject.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
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
}
