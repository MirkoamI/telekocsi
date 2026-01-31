package telekocsi.aspect_ride;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import telekocsi.model.Ride;

@Aspect
@Component
public class LoggingAspectGetRideById {
    private final Logger logInfo = LoggerFactory.getLogger("fileLoggerInfo");
    @Around("execution(* telekocsi.service.RideService.getRideById(..)) && args(id)")
    public Object logAroundGetRideById(ProceedingJoinPoint joinPoint, long id) throws Throwable {
        logInfo.info("Method calling: {} - Fetching ride ID={} started.", joinPoint.getSignature().getName(), id);
        Object result;
        try {
            result = joinPoint.proceed();
            if (result instanceof Ride ride) {
                String driverName = (ride.getDriver() != null) ? ride.getDriver().getName() : "Unknown";
                logInfo.info("Ride successfully fetched: ID={}, {} -> {}, driver={}",
                        ride.getId(), ride.getStartPlace(), ride.getEndPlace(), driverName);
            }
        } catch (Exception ex) {
            logInfo.error("Error occurred while fetching ride ID={}: {}", id, ex.getMessage());
            throw ex;
        }
        return result;
    }
}