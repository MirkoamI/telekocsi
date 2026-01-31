package telekocsi.aspect_ride;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspectAddPassenger {

    private final Logger logInfo = LoggerFactory.getLogger("fileLoggerInfo");
    @Around("execution(* telekocsi.service.RideService.addPassengerToRide(..)) && args(rideId, userId)")
    public Object logAroundAddPassenger(ProceedingJoinPoint joinPoint, long rideId, long userId) throws Throwable {
        logInfo.info("Method calling: {} - Adding passenger Ride ID={} begin. Passenger ID: {}",
                joinPoint.getSignature().getName(), rideId, userId);

        Object result;
        try {
            result = joinPoint.proceed();
            logInfo.info("Passenger (ID={}) successfully added to Ride ID={}", userId, rideId);
        } catch (Exception ex) {
            logInfo.error("Error occurred while adding passenger to Ride ID={} Passenger ID={}: {}", rideId, userId, ex.getMessage());
            throw ex;
        }
        return result;
    }
}