package com.shop.backend.aop;

import com.shop.backend.dto.LoginRequest;
import com.shop.backend.dto.LoginResponse;
import com.shop.backend.model.User;
import com.shop.backend.security.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * AOP切面用于记录登录端点的日志
 * 记录进入/离开登录端点的时间、JWT信息和用户信息
 */
@Aspect
@Component
@Slf4j
public class LoginLoggingAspect {

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 定义切入点：匹配AuthController中的authenticateUser方法
     */
    @Pointcut("execution(* com.shop.backend.controller.AuthController.authenticateUser(..))")
    public void loginEndpoint() {}

    /**
     * 在方法执行前记录日志
     */
    @Before("loginEndpoint()")
    public void logBeforeLogin(JoinPoint joinPoint) {
        log.info("=== LOGIN REQUEST STARTED ===");
        log.info("Timestamp: {}", LocalDateTime.now());
        
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof LoginRequest) {
            LoginRequest loginRequest = (LoginRequest) args[0];
            log.info("Username: {}", loginRequest.getUsername());
            // 注意：出于安全考虑，不记录密码
        }
        
        log.info("Method: {}", joinPoint.getSignature().toShortString());
    }

    /**
     * 在方法执行后记录日志（无论成功或异常）
     */
    @After("loginEndpoint()")
    public void logAfterLogin(JoinPoint joinPoint) {
        log.info("=== LOGIN REQUEST COMPLETED ===");
        log.info("Timestamp: {}", LocalDateTime.now());
        log.info("Method: {}", joinPoint.getSignature().toShortString());
    }

    /**
     * 在方法成功返回后记录日志，包含JWT信息和用户信息
     */
    @AfterReturning(pointcut = "loginEndpoint()", returning = "result")
    public void logAfterSuccessfulLogin(JoinPoint joinPoint, Object result) {
        if (result instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
            Object body = responseEntity.getBody();
            
            if (body instanceof LoginResponse) {
                LoginResponse loginResponse = (LoginResponse) body;
                
                log.info("=== LOGIN SUCCESS DETAILS ===");
                log.info("User ID: {}", loginResponse.getId());
                log.info("Username: {}", loginResponse.getUsername());
                log.info("Email: {}", loginResponse.getEmail());
                log.info("Role: {}", loginResponse.getRole());
                
                // 记录JWT信息
                String jwt = loginResponse.getToken();
                String refreshToken = loginResponse.getRefreshToken();
                
                if (jwt != null) {
                    log.info("JWT Token generated (first 20 chars): {}...", 
                            jwt.substring(0, Math.min(jwt.length(), 20)));
                    
                    try {
                        String usernameFromJwt = jwtUtils.getUserNameFromToken(jwt);
                        Long userIdFromJwt = jwtUtils.getUserIdFromToken(jwt);
                        log.info("JWT contains - Username: {}, User ID: {}", usernameFromJwt, userIdFromJwt);
                    } catch (Exception e) {
                        log.warn("Could not parse JWT for logging: {}", e.getMessage());
                    }
                }
                
                if (refreshToken != null) {
                    log.info("Refresh Token (first 20 chars): {}...", 
                            refreshToken.substring(0, Math.min(refreshToken.length(), 20)));
                }
                
                log.info("Login successful at: {}", LocalDateTime.now());
            }
        }
    }

    /**
     * 在方法抛出异常后记录日志
     */
    @AfterThrowing(pointcut = "loginEndpoint()", throwing = "error")
    public void logAfterLoginException(JoinPoint joinPoint, Throwable error) {
        log.error("=== LOGIN FAILED ===");
        log.error("Timestamp: {}", LocalDateTime.now());
        log.error("Method: {}", joinPoint.getSignature().toShortString());
        log.error("Error: {}", error.getMessage());
        log.error("Error type: {}", error.getClass().getName());
        
        // 记录堆栈跟踪的前几行
        StackTraceElement[] stackTrace = error.getStackTrace();
        if (stackTrace.length > 0) {
            log.error("Stack trace (first 3 lines):");
            for (int i = 0; i < Math.min(3, stackTrace.length); i++) {
                log.error("  {}", stackTrace[i]);
            }
        }
    }
}