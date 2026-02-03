package com.github.xujia118.simpletaskmanager.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around("execution(* com.github.xujia118.simpletaskmanager.controller..*(..)) || execution(* org.springframework.web.client.RestTemplate.*(..))")
    public Object logExecutionTimeAndDetails(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            logger.info(">>> HTTP Request: [{} {}] | Method: {}.{}",
                    request.getMethod(), request.getRequestURL(),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        }

        Object proceed;
        try {
            proceed = joinPoint.proceed();
        } catch (Throwable e) {
            logger.error("Exception in {}.{} with cause: {}",
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
                    e.getMessage());
            throw e;
        }

        long executionTime = System.currentTimeMillis() - start;
        logger.info("<<< Response: {} | Execution Time: {}ms", proceed, executionTime);

        return proceed;
    }

    @Before("execution(* com.github.xujia118.simpletaskmanager.controller.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Direct Binding - Before: Executing {}", joinPoint.getSignature().toShortString());
    }

    @AfterReturning(pointcut = "execution(* com.github.xujia118.simpletaskmanager.service.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Direct Binding - After Success: {} returned {}", joinPoint.getSignature().getName(), result);
    }
}
