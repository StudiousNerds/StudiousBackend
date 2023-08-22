package nerds.studiousTestProject.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증이 실패한 상황(403)을 처리하는 AuthenticationEntryPoint 인터페이스의 구현 클래스
 * 보통 잘못된 URL에 접근하는 경우
 */
@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        switch (response.getStatus()) {
            // 컨트롤러에 매핑되지 않는 URI로 접속한 경우 (왜 200도 여기에 포함되야되는진 모르겠음,,, 포스트맨으로 테스트해보니간 매핑안된 URI가 200으로 응답을 해버려서)
            case HttpServletResponse.SC_BAD_REQUEST, HttpServletResponse.SC_NOT_FOUND -> {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(new ObjectMapper().writeValueAsString(ExceptionResponse.from(ErrorCode.NOT_EXIST_PAGE)));
                log.info(LOG_FORMAT, authException.getClass().getSimpleName(), ErrorCode.NOT_EXIST_PAGE, ErrorCode.NOT_EXIST_PAGE.getMessage());
            }
            default -> response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);    // 이 경우에도 로그를 찍어야 함
        }

    }
}
