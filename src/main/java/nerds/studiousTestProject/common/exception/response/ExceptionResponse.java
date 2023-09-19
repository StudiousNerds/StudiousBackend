package nerds.studiousTestProject.common.exception.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.common.exception.CustomException;
import nerds.studiousTestProject.common.exception.errorcode.ErrorCode;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExceptionResponse {
    private String message;
    private String code;

    public static ExceptionResponse from(ErrorCode errorCode) {
        return ExceptionResponse.builder()
                .message(errorCode.getMessage())
                .code(errorCode.name())
                .build();
    }

    public static ExceptionResponse from(String message, String code) {
        return ExceptionResponse.builder()
                .message(message)
                .code(code)
                .build();
    }

    public static ExceptionResponse from(CustomException e) {
        return ExceptionResponse.builder()
                .message(e.getMessage())
                .code(e.getErrorCode().name())
                .build();
    }
}
