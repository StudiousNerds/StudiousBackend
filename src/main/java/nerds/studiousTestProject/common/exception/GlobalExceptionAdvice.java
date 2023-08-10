package nerds.studiousTestProject.common.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static nerds.studiousTestProject.common.exception.ErrorCode.INVALID_REQUEST_BODY_TYPE;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

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

        // 잘못된 uri가 경로 변수가 포함된 메소드에 매핑되버리는 경우는 여기서 처리되긴하는데... 404를 보내는게 맞는거같기도 하고,,,

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

    @ExceptionHandler(value = MissingRequestHeaderException.class)
    public ResponseEntity<ExceptionResponse> headerExceptionHandler(MissingRequestHeaderException e) {
        String headerName = e.getHeaderName();
        HeaderErrorCode headerErrorCode = HeaderErrorCode.of(headerName);
        String code = headerErrorCode.name();
        String message = headerErrorCode.getMessage();

        log.info(LOG_FORMAT, e.getClass().getSimpleName(), code, message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.from(message, code));
    }

    @ExceptionHandler(value = WebClientResponseException.class)
    public ResponseEntity<ExceptionResponse> webClientExceptionHandler(WebClientResponseException e) {
        String code = ErrorCode.WEB_CLIENT_ERROR.name();
        String message = ErrorCode.WEB_CLIENT_ERROR.getMessage();
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), code, e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.from(message, code));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationOfRequestBodyException(MethodArgumentNotValidException e) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(e.getClass().getSimpleName()).append(INVALID_REQUEST_BODY_TYPE.getMessage()).append(e.getMessage());
        log.info(stringBuilder.toString());
        stringBuilder.setLength(0); //stringBuilder 초기화
        e.getBindingResult().getAllErrors().forEach((error) -> stringBuilder.append(error.getDefaultMessage())
                .append(System.lineSeparator()));
        return ResponseEntity.badRequest()
                .body(ExceptionResponse.from(stringBuilder.toString(), INVALID_REQUEST_BODY_TYPE.name()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException e){
        StringBuilder stringBuilder = new StringBuilder();
        log.info(stringBuilder.append(e.getClass().getSimpleName()).append(e.getErrorCode().getMessage()).append(e.getMessage()).toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ExceptionResponse.from(e));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidRequestType(BadRequestException e){
        StringBuilder stringBuilder = new StringBuilder();
        log.info(stringBuilder.append(e.getClass().getSimpleName()).append(e.getErrorCode().getMessage()).append(e.getMessage()).toString());
        return ResponseEntity.badRequest().body(ExceptionResponse.from(e));
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<ExceptionResponse> handleUnAuthorizedException(NotAuthorizedException e) {
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(UNAUTHORIZED).body(ExceptionResponse.from(e));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {

        MethodErrorCode methodErrorCode = MethodErrorCode.of(e.getMethod());
        String code = methodErrorCode.name();
        String message = methodErrorCode.getMessage();
        String entryMessage = methodErrorCode.getEntryMessage(e.getSupportedMethods());

        log.info(LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return ResponseEntity.status(METHOD_NOT_ALLOWED).body(ExceptionResponse.from(entryMessage, code));
    }

    /**
     * &#064;RequestBody 에서 (타입 오류 등의 이유로 Json Parser가) 바인딩 실패 시 호출되는 예외를 핸들링
     * @param e HttpMessageNotReadableException
     * @return 예외 메시지, 상태 코드를 담은 응답
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), NOT_PARSING_BODY.name(), NOT_PARSING_BODY.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(ExceptionResponse.from(NOT_PARSING_BODY));
    }
}
