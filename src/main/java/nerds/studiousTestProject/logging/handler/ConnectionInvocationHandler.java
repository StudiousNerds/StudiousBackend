package nerds.studiousTestProject.logging.handler;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.logging.util.ApiQueryCounter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@RequiredArgsConstructor
public class ConnectionInvocationHandler implements InvocationHandler {
    private final ApiQueryCounter apiQueryCounter;
    private final Object connection;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = method.invoke(connection, args);
        if (method.getName().equals("prepareStatement")) {
            return Proxy.newProxyInstance(
                    result.getClass().getClassLoader(),
                    result.getClass().getInterfaces(),
                    new PreparedStatementInvocationHandler(apiQueryCounter, result)
            );
        }

        return result;
    }
}
