package co.mizrahi.currency.conversion.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created at 20/09/2024
 *
 * @author David Mizrahi
 */
@Slf4j
@Aspect
@Component
@SuppressWarnings("ALL")
@RequiredArgsConstructor
public class LoggingAspect {

    private final Map<String, RequestLoggingService> requestLoggingServices;

    @Around("@annotation(co.mizrahi.currency.conversion.logging.WriteResponseToDB)")
    public Object logToDatabase(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        var annotation = method.getAnnotation(WriteResponseToDB.class);
        var loggerId = annotation.loggerId();
        var body = joinPoint.proceed();
        this.requestLoggingServices.get(loggerId).logRequest(body);
        return body;
    }
}
