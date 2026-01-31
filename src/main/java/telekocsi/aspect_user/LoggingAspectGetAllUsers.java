package telekocsi.aspect_user;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class LoggingAspectGetAllUsers {

    private final Logger logInfo = LoggerFactory.getLogger("fileLoggerInfo");
    private final Logger logError = LoggerFactory.getLogger("fileLoggerError");

    @Around("execution(* telekocsi.service.UserService.getAllUsers(..))")
    public Object logAroundGetAllUsers(ProceedingJoinPoint joinPoint) throws Throwable {
        logInfo.info("Method calling: {} - Fetching all users started.",
                joinPoint.getSignature().getName());

        Object result;
        try {
            result = joinPoint.proceed();

            if (result instanceof List<?> list) {
                if (!list.isEmpty()) {
                    logInfo.info("Users successfully fetched. Total users: {}.", list.size());
                } else {
                    logInfo.info("The user list is empty.");
                }
            } else {
                logInfo.info("No data returned.");
            }

        } catch (Exception ex) {
            logError.error("Error occurred while fetching all users: {}", ex.getMessage());
            throw ex;
        }

        return result;
    }
}