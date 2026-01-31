package telekocsi.aspect_user;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspectDeleteUser {

    private final Logger logInfo = LoggerFactory.getLogger("fileLoggerInfo");
    private final Logger logError = LoggerFactory.getLogger("fileLoggerError");

    @Around(value = "execution(* telekocsi.service.UserService.deleteUserById(..)) && args(id)", argNames = "joinPoint,id")
    public Object logAroundDeleteUser(ProceedingJoinPoint joinPoint, Long id) throws Throwable {
        logInfo.info("Method calling: {} - Deletion of user ID={} started.",
                joinPoint.getSignature().getName(), id);

        try {
            Object result = joinPoint.proceed();
            logInfo.info("User successfully deleted: ID={}.", id);
            return result;
        } catch (Exception ex) {
            logError.error("Error occurred while deleting user ID={}: {}", id, ex.getMessage());
            throw ex;
        }
    }
}