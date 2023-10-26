package nerds.studiousTestProject.common.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

@Component
@RequiredArgsConstructor
public class HttpRequestEndPointChecker {
    private final DispatcherServlet dispatcherServlet;

    public boolean existEndPoint(HttpServletRequest request) {
        for (HandlerMapping handlerMapping : dispatcherServlet.getHandlerMappings()) {
            try {
                HandlerExecutionChain foundHandler = handlerMapping.getHandler(request);
                if (foundHandler != null) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }
}
