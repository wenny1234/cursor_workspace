package com.shop.backend.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 通用的Controller日志切面
 * 记录所有Controller方法的进入和退出
 */
@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    /**
     * 切入点：匹配所有Controller类（带有@RestController注解的类）
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerClass() {}

    /**
     * 切入点：匹配所有Controller类中的public方法
     */
    @Pointcut("execution(public * *(..))")
    public void publicMethod() {}

    /**
     * 组合切入点：Controller类中的public方法
     */
    @Pointcut("controllerClass() && publicMethod()")
    public void controllerMethod() {}

    /**
     * 在方法执行前记录日志
     */
    @Before("controllerMethod()")
    public void logBeforeControllerMethod(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        log.info("=== CONTROLLER METHOD ENTRY ===");
        log.info("Timestamp: {}", LocalDateTime.now());
        log.info("Controller: {}", className);
        log.info("Method: {}", methodName);
        
        // 记录请求参数（排除敏感信息如密码）
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            log.info("Parameters count: {}", args.length);
            
            // 记录参数类型
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg != null) {
                    String argType = arg.getClass().getSimpleName();
                    // 对于敏感参数（如LoginRequest），不记录具体内容
                    if (argType.contains("Request") && !methodName.contains("login")) {
                        log.info("  Arg[{}]: {} (type: {})", i, "***SENSITIVE DATA***", argType);
                    } else {
                        log.info("  Arg[{}]: {} (type: {})", i, 
                                arg.toString().length() > 100 ? 
                                arg.toString().substring(0, 100) + "..." : arg.toString(), 
                                argType);
                    }
                } else {
                    log.info("  Arg[{}]: null", i);
                }
            }
        }
        
        // 检查方法上的注解
        try {
            Class<?> targetClass = joinPoint.getTarget().getClass();
            java.lang.reflect.Method method = Arrays.stream(targetClass.getMethods())
                    .filter(m -> m.getName().equals(methodName) && 
                           m.getParameterCount() == args.length)
                    .findFirst()
                    .orElse(null);
            
            if (method != null) {
                if (method.isAnnotationPresent(GetMapping.class)) {
                    log.info("HTTP Method: GET");
                } else if (method.isAnnotationPresent(PostMapping.class)) {
                    log.info("HTTP Method: POST");
                    GetMapping getMapping = method.getAnnotation(GetMapping.class);
                    if (getMapping != null && getMapping.value().length > 0) {
                        log.info("Endpoint: {}", Arrays.toString(getMapping.value()));
                    }
                } else if (method.isAnnotationPresent(PutMapping.class)) {
                    log.info("HTTP Method: PUT");
                } else if (method.isAnnotationPresent(DeleteMapping.class)) {
                    log.info("HTTP Method: DELETE");
                } else if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    if (requestMapping.method().length > 0) {
                        log.info("HTTP Method: {}", Arrays.toString(requestMapping.method()));
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Could not determine HTTP method: {}", e.getMessage());
        }
    }

    /**
     * 在方法执行后记录日志（无论成功或异常）
     */
    @After("controllerMethod()")
    public void logAfterControllerMethod(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        log.info("=== CONTROLLER METHOD EXIT ===");
        log.info("Timestamp: {}", LocalDateTime.now());
        log.info("Controller: {}", className);
        log.info("Method: {}", methodName);
        log.info("Execution completed");
    }

    /**
     * 在方法成功返回后记录日志
     */
    @AfterReturning(pointcut = "controllerMethod()", returning = "result")
    public void logAfterControllerMethodSuccess(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        log.info("=== CONTROLLER METHOD SUCCESS ===");
        log.info("Controller: {}", className);
        log.info("Method: {}", methodName);
        
        if (result != null) {
            String resultType = result.getClass().getSimpleName();
            log.info("Return type: {}", resultType);
            
            // 对于响应实体，记录状态码
            if (result instanceof org.springframework.http.ResponseEntity) {
                org.springframework.http.ResponseEntity<?> responseEntity = 
                    (org.springframework.http.ResponseEntity<?>) result;
                log.info("HTTP Status: {}", responseEntity.getStatusCode());
            }
            
            // 限制日志输出长度
            String resultStr = result.toString();
            if (resultStr.length() > 200) {
                log.info("Return value: {}...", resultStr.substring(0, 200));
            } else {
                log.info("Return value: {}", resultStr);
            }
        } else {
            log.info("Return value: null");
        }
    }

    /**
     * 在方法抛出异常后记录日志
     */
    @AfterThrowing(pointcut = "controllerMethod()", throwing = "error")
    public void logAfterControllerMethodException(JoinPoint joinPoint, Throwable error) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        log.error("=== CONTROLLER METHOD EXCEPTION ===");
        log.error("Timestamp: {}", LocalDateTime.now());
        log.error("Controller: {}", className);
        log.error("Method: {}", methodName);
        log.error("Exception: {}", error.getClass().getName());
        log.error("Message: {}", error.getMessage());
        
        // 记录堆栈跟踪的前5行
        StackTraceElement[] stackTrace = error.getStackTrace();
        if (stackTrace.length > 0) {
            log.error("Stack trace (first 5 lines):");
            for (int i = 0; i < Math.min(5, stackTrace.length); i++) {
                log.error("  {}", stackTrace[i]);
            }
        }
    }
}