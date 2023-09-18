package nerds.studiousTestProject.common.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.NoHandlerFoundException;

import static nerds.studiousTestProject.common.exception.ErrorCode.INVALID_REQUEST_BODY_TYPE;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_PAGE;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_PARSING_BODY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {
    private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";

    /**
     * 1. MethodArgumentTypeMismatchException
     * &#064;PathVariable 또는 @RequestParam 에서 타입 바인딩 오류시 발생하는 예외를 핸들링
     * @param e MethodArgumentTypeMismatchException
     * @return 예외 메시지, 상태 코드를 담은 응답
     *
     * 2. MissingServletRequestParameterException
     * &#064;RequestParam 중 필수(require = true)가 설정된 파라미터가 누락된 경우 발생하는 예외 핸들링
     * @param e MissingServletRequestParameterException
     * @return 예외 메시지, 상태 코드를 담은 응답
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(Exception e) {
        String param = e instanceof MethodArgumentTypeMismatchException ? ((MethodArgumentTypeMismatchException) e).getName() : ((MissingServletRequestParameterException) e).getParameterName();

        ParamErrorCode paramErrorCode = ParamErrorCode.of(param);
        String code = paramErrorCode.name();
        String message = paramErrorCode.getMessage();

        log.info(LOG_FORMAT, e.getClass().getSimpleName(), code, message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.from(message, code));
    }

    /**
     * @RequestParam의 @NotNull, @Size 등 기본 어노테이션 검증에 실패한 경우 발생하는 예외 핸들링
     * ConstraintViolationException 예외인 경우 예외 메시지를 직접 파싱하여 파라미터 이름을 찾아야 함... => 이 방법은 추후 리펙토링 예정
     * @param e ConstraintViolationException
     * @return 예외 메시지, 상태 코드를 담은 응답
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException e) {
        String param = e.getMessage().split(" ")[0].split("\\.")[1].replace(":", "");
        String message = e.getMessage().split(":")[1].trim();

        log.info(e.getMessage());

        ParamErrorCode paramErrorCode = ParamErrorCode.of(param);
        String code = paramErrorCode.name();

        log.info(LOG_FORMAT, e.getClass().getSimpleName(), code, message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.from(message, code));
    }

    /**
     * @RequestHeader 값이 누락된 경우 발생하는 예외 핸들링
     * @param e MissingRequestHeaderException
     * @return 예외 메시지, 상태 코드를 담은 응답
     */
    @ExceptionHandler(value = MissingRequestHeaderException.class)
    public ResponseEntity<ExceptionResponse> handleHeaderException(MissingRequestHeaderException e) {
        String headerName = e.getHeaderName();
        HeaderErrorCode headerErrorCode = HeaderErrorCode.of(headerName);
        String code = headerErrorCode.name();
        String message = headerErrorCode.getMessage();

        log.info(LOG_FORMAT, e.getClass().getSimpleName(), code, message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.from(message, code));
    }

    @ExceptionHandler(value = WebClientResponseException.class)
    public ResponseEntity<ExceptionResponse> handleWebClientException(WebClientResponseException e) {
        String code = ErrorCode.WEB_CLIENT_ERROR.name();
        String message = ErrorCode.WEB_CLIENT_ERROR.getMessage();
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), code, e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.from(message, code));
    }

    /**
     * &#064;ModelAttribute 에서 객체 바인딩 또는 @ModelAttribute, @RequestBody 에서 @Valid 검증 실패 시 호출되는 예외 핸들링 메소드
     * @param e MethodArgumentNotValidException
     * @return 예외 메시지, 상태 코드를 담은 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(e.getClass().getSimpleName()).append(INVALID_REQUEST_BODY_TYPE.getMessage()).append(e.getMessage());
//        log.info(stringBuilder.toString());
//        stringBuilder.setLength(0); //stringBuilder 초기화

        String message = String.join(", ", e.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage).toArray(String[]::new));

//        e.getBindingResult().getAllErrors().forEach((objectError -> stringBuilder.append(objectError.getDefaultMessage()).append(System.lineSeparator())));
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), INVALID_REQUEST_BODY_TYPE.name(), message);

        return ResponseEntity.badRequest()
                .body(ExceptionResponse.from(message, INVALID_REQUEST_BODY_TYPE.name()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException e){
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(NOT_FOUND).body(ExceptionResponse.from(e));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidRequestType(BadRequestException e){
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), e.getErrorCode(), e.getMessage());
        return ResponseEntity.badRequest().body(ExceptionResponse.from(e));
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<ExceptionResponse> handleUnAuthorizedException(NotAuthorizedException e) {
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(UNAUTHORIZED).body(ExceptionResponse.from(e));
    }

    /**
     * 잘못된 메소드로 요청이 왔을 때 발생하는 예외를 핸들링
     * @param e HttpRequestMethodNotSupportedException
     * @return 예외 메시지, 상태 코드를 담은 응답
     */
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
