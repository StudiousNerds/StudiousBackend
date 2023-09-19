package nerds.studiousTestProject.common.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.response.ExceptionResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_PAGE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_HAVE_PERMISSION;

/**
 * 인증이 실패한 상황(403)을 처리하는 AuthenticationEntryPoint 인터페이스의 구현 클래스
 * 보통 잘못된 URL에 접근하는 경우
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final HttpRequestEndPointChecker httpRequestEndPointChecker;
    private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        if (!httpRequestEndPointChecker.existEndPoint(request)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(new ObjectMapper().writeValueAsString(ExceptionResponse.from(NOT_FOUND_PAGE)));
            log.info(LOG_FORMAT, authException.getClass().getSimpleName(), NOT_FOUND_PAGE, NOT_FOUND_PAGE.getMessage());
            return;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(new ObjectMapper().writeValueAsString(ExceptionResponse.from(NOT_HAVE_PERMISSION)));
        log.info(LOG_FORMAT, authException.getClass().getSimpleName(), NOT_HAVE_PERMISSION, NOT_HAVE_PERMISSION.getMessage());
    }
}
