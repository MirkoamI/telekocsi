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
public class LoggingAspectCreateUser {
    private final Logger logInfo = LoggerFactory.getLogger("fileLoggerInfo");
    @Around(value = "execution(* telekocsi.service.UserService.registerUser(..))", argNames = "joinPoint")
    public Object logAroundCreateUser(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        User user = (args.length > 0 && args[0] instanceof User) ? (User) args[0] : null;
        if (user != null) {
            logInfo.info(
                    "Method calling: {} - Creating new user started: [name: '{}', age: {}, email: '{}', phone: '{}']",
                    joinPoint.getSignature().getName(),
                    user.getName(),
                    user.getAge(),
                    user.getEmail(),
                    user.getPhone()
            );
        }
        Object result = joinPoint.proceed();
        if (result instanceof User createdUser) {
            logInfo.info("User successfully created: ID={}, name='{}'", createdUser.getId(), createdUser.getName());
        }

        return result;
    }
}