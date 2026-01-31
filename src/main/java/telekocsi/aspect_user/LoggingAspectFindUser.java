package telekocsi.aspect_user;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import telekocsi.model.User;

@Aspect
@Component
public class LoggingAspectFindUser {
    private final Logger logInfo = LoggerFactory.getLogger("fileLoggerInfo");

    @Around(value = "execution(* telekocsi.service.UserService.findUserById(..)) && args(id)", argNames = "joinPoint,id")
    public Object logAroundGetItem(ProceedingJoinPoint joinPoint, Long id) throws Throwable {
        logInfo.info("Method calling: {} - Fetching user ID={} started.", joinPoint.getSignature().getName(), id);

        Object result;
        try {
            result = joinPoint.proceed();
            if (result instanceof User user) {
                logInfo.info("User successfully fetched: [ID={}, name='{}']", user.getId(), user.getName());
            } else {
                logInfo.info("User fetched, but result is null or unknown.");
            }

        } catch (Exception ex) {
            logInfo.error("Error occurred while fetching user ID={}: {}", id, ex.getMessage());
            throw ex;
        }

        return result;
    }
}