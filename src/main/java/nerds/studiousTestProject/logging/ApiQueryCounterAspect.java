package nerds.studiousTestProject.logging;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.logging.handler.ConnectionInvocationHandler;
import nerds.studiousTestProject.logging.util.ApiQueryCounter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

@Aspect
@Component
@RequiredArgsConstructor
public class ApiQueryCounterAspect {
    private final ApiQueryCounter apiQueryCounter;

    @Around("execution(* javax.sql.DataSource.getConnection())")
    public Object getConnection(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object connection = proceedingJoinPoint.proceed();
        return Proxy.newProxyInstance(
                connection.getClass().getClassLoader(),
                connection.getClass().getInterfaces(),
                new ConnectionInvocationHandler(apiQueryCounter, connection)
        );
    }
}
