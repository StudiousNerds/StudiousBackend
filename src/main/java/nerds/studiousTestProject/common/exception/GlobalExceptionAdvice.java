package nerds.studiousTestProject.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static nerds.studiousTestProject.common.exception.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

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
}
