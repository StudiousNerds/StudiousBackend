package nerds.studiousTestProject.studycafe.exception.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.studycafe.exception.message.ParamExceptionMessage;
import nerds.studiousTestProject.studycafe.exception.model.SearchException;
import nerds.studiousTestProject.user.exception.dto.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class SearchExceptionHandlerAdvice {

    /**
     * 컨트롤러 및 DTO 단에서 예외 처리
     * @param e ConstraintViolationException 또는 MethodArgumentTypeMismatchException
     * @return
     */
    @ExceptionHandler(value = {ConstraintViolationException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ExceptionDto> paramExceptionHandler(Exception e) {
        // ConstraintViolationException 예외인 경우 예외 메시지를 직접 파싱하여 파라미터 이름을 찾아야 함... => 이 방법은 추후 리펙토링 예정
        // ex) search.request : XXX 형태에서 request 를 가져와야함
        String param = e instanceof MethodArgumentTypeMismatchException ?
                ((MethodArgumentTypeMismatchException) e).getName() :
                e.getMessage().split(" ")[0].split("\\.")[1].replace(":", "");

        log.info("msg = {}", e.getMessage());
        log.info("paramName = {}", param);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionDto.builder()
                        .message(ParamExceptionMessage.of(param).getMessage())
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build()
                );
    }

    /**
     * 서비스 레이어에서 예외 처리
     * @param e SearchException
     * @return
     */
    @ExceptionHandler(value = SearchException.class)
    public ResponseEntity<ExceptionDto> searchExceptionHandler(SearchException e) {
        log.info("msg = {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionDto.builder()
                        .message(e.getMessage())
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build()
                );
    }
}
