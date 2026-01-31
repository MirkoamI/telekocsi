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
public class LoggingAspectRegisterRide {
    private final Logger logInfo = LoggerFactory.getLogger("fileLoggerInfo");
    @Around("execution(* telekocsi.service.RideService.registerRide(..)) && args(ride)")
    public Object logAroundRegisterRide(ProceedingJoinPoint joinPoint, Ride ride) throws Throwable {
        boolean isUpdate = (ride.getId() != null && ride.getId() > 0);
        String action = isUpdate ? "updating" : "creation";

        String driverName = (ride.getDriver() != null) ? ride.getDriver().getName() : "Unknown";

        logInfo.info("Method calling: {} - Ride {} started. {} -> {}, driver={}",
                joinPoint.getSignature().getName(), action, ride.getStartPlace(), ride.getEndPlace(), driverName);

        Object result;
        try {
            result = joinPoint.proceed();
            if (result instanceof Ride processedRide) {
                String processedDriverName = (processedRide.getDriver() != null) ? processedRide.getDriver().getName() : "Unknown";
                logInfo.info("Ride successfully {}: ID={}, {} -> {}, driver={}",
                        (isUpdate ? "updated" : "created"),
                        processedRide.getId(), processedRide.getStartPlace(), processedRide.getEndPlace(), processedDriverName);
            }
        } catch (Exception ex) {
            logInfo.error("Error occurred during ride {}: {} -> {}: {}",
                    (isUpdate ? "update" : "creation"),
                    ride.getStartPlace(), ride.getEndPlace(), ex.getMessage());
            throw ex;
        }

        return result;
    }
}