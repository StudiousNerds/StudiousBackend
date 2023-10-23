package nerds.studiousTestProject.logging.handler;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.logging.util.ApiQueryCounter;
import org.springframework.web.context.request.RequestContextHolder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@RequiredArgsConstructor
public class PreparedStatementInvocationHandler implements InvocationHandler {
    private final ApiQueryCounter apiQueryCounter;
    private final Object preparedStatement;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().contains("execute") && RequestContextHolder.getRequestAttributes() != null) {
            apiQueryCounter.increaseCount();
        }

        return method.invoke(preparedStatement, args);
    }
}
