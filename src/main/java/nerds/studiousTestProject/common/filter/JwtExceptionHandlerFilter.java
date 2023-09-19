package nerds.studiousTestProject.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.response.ExceptionResponse;
import nerds.studiousTestProject.common.exception.NotAuthorizedException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {
    private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (BadRequestException | NotAuthorizedException e) {
            int status = e instanceof BadRequestException ? HttpServletResponse.SC_BAD_REQUEST : HttpServletResponse.SC_UNAUTHORIZED;
            response.setStatus(status);
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.getWriter().write(new ObjectMapper().writeValueAsString(ExceptionResponse.from(e)));
            log.info(LOG_FORMAT, e.getClass().getSimpleName(), e.getErrorCode(), e.getMessage());
        }
    }
}
