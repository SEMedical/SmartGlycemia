package edu.tongji.backend.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class TestAspect {
    @Pointcut("@annotation(org.junit.jupiter.api.Test)")
    void testsets(){}
    @Before("testsets()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 开始打印请求日志
        Signature signature = joinPoint.getSignature();
        log.info("========================================== Start Test {}==========================================",signature.getName());
        // 打印请求入参
        Object[] args = joinPoint.getArgs();
        int length = args.length;
        log.info("Request Args:");
        for (int i=0;i<length;i++) {
            log.info("Arg {}: {}",i, args[i]);
        }
    }
    @After("testsets()")
    public void doAfter(JoinPoint joinPoint) throws Throwable{
        Signature signature = joinPoint.getSignature();
        try{
            log.error("================================= Test {} passed===================",signature.getName());
        }catch (Exception e){
            log.error("================================= Test {} failed====================",signature.getName());
        }
    }
    @Around("testsets()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        //开始时间
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        log.info("Test time-Consuming : {} ms", System.currentTimeMillis() - startTime);
        return result;
    }

}
