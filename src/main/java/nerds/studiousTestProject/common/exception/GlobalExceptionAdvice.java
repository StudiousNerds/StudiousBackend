package nerds.studiousTestProject.common.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {
    private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";

    /**
     * 컨트롤러 및 DTO 단에서 예외 처리
     * @param e ConstraintViolationException 또는 MethodArgumentTypeMismatchException
     * @return
     */
    @ExceptionHandler(value = {ConstraintViolationException.class, MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ExceptionResponse> paramExceptionHandler(Exception e) {
        // ConstraintViolationException 예외인 경우 예외 메시지를 직접 파싱하여 파라미터 이름을 찾아야 함... => 이 방법은 추후 리펙토링 예정
        // ex) search.request : XXX 형태에서 request 를 가져와야함

        String param = e instanceof MethodArgumentTypeMismatchException ?
                ((MethodArgumentTypeMismatchException) e).getName() : (e instanceof MissingServletRequestParameterException) ? ((MissingServletRequestParameterException) e).getParameterName() :
                e.getMessage().split(" ")[0].split("\\.")[1].replace(":", "");

        ParamErrorCode paramErrorCode = ParamErrorCode.of(param);
        String code = paramErrorCode.name();
        String message = paramErrorCode.getMessage();
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), code, message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.from(message, code));
    }

    @ExceptionHandler(value = WebClientResponseException.class)
    public ResponseEntity<ExceptionResponse> webClientExceptionHandler(WebClientResponseException e) {
        String code = ErrorCode.WEB_CLIENT_ERROR.name();
        String message = ErrorCode.WEB_CLIENT_ERROR.getMessage();
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), code, message);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.from(message, code));
    }
}
