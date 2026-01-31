package telekocsi.aspect_ride;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@Aspect
@Component
public class LoggingAspectSearchRides {

    private final Logger logInfo = LoggerFactory.getLogger("fileLoggerInfo");

    @Around("execution(* telekocsi.service.RideService.searchRides(..)) && args(from, to, date)")
    public Object logAroundSearchRides(ProceedingJoinPoint joinPoint, String from, String to, LocalDate date) throws Throwable {
        String fromLog = (from != null && !from.isEmpty()) ? from : "*";
        String toLog = (to != null && !to.isEmpty()) ? to : "*";
        String dateLog = (date != null) ? date.toString() : "anytime";

        logInfo.info("Search started: From: '{}', To: '{}', When: '{}'", fromLog, toLog, dateLog);

        Object result = joinPoint.proceed();

        if (result instanceof List<?> list) {
            logInfo.info("Search result: {} matches.", list.size());
        }

        return result;
    }
}