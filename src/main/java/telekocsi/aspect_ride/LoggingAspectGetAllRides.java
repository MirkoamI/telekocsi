package telekocsi.aspect_ride;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.List;

@Aspect
@Component
public class LoggingAspectGetAllRides {

    private final Logger logInfo = LoggerFactory.getLogger("fileLoggerInfo");

    @Around("execution(* telekocsi.service.RideService.getAllRides(..))")
    public Object logAroundGetAllRides(ProceedingJoinPoint joinPoint) throws Throwable {
        logInfo.info("Method calling: {} - Fetching all rides started.", joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();

        if (result instanceof List<?> list) {
            if (!list.isEmpty()) {
                logInfo.info("All rides successfully fetched. Total: {}.", list.size());
            } else {
                logInfo.info("No rides found in the database.");
            }
        }
        return result;
    }
}