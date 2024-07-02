package edu.tongji.backend.aspect;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 Victor Hu
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */




import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.session.StandardSessionFacade;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
@Component
@Slf4j
public class WebLogAspect {
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PostMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.PutMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.GetMapping)"+
            "|| @annotation(java.lang.Override)")
    public void requestMapping() {
    }

    @Pointcut("@within(org.springframework.stereotype.Controller)|| @within(org.springframework.web.bind.annotation.RestController)")
    public void controller(){}
    @Before("requestMapping()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 打印请求相关参数
        log.info("========================================== Start ==========================================");
        // 打印请求 url
        log.info("URL            : {}", request.getRequestURL().toString());
        // 打印 Http method
        log.info("HTTP Method    : {}", request.getMethod());
        // 打印调用 controller 的全路径以及执行方法
        log.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        // 打印请求的 IP
        log.info("IP             : {}", request.getRemoteAddr());
        // 打印请求入参
        Object[] args = joinPoint.getArgs();
        int length = args.length;
        log.info("Request Args:");
        for (int i=0;i<length;i++) {
            if(!(args[i] instanceof StandardSessionFacade))
                log.info("Arg {}: {}",i, maskSecrets(args[i].toString()));
            else
                log.info("Session Content:---");
        }
    }
    public static String maskSecrets(String input) {
        String chineseNamePattern = "(\\\"name\\\":\\\")([^\\\"]*)(\\\")";

        String phoneNumberPattern = "(\\d{3})\\d{4}(\\d{4})";
        String captchaPattern="(?<!\\d)\\d{6}(?!\\d)";

        Pattern chineseNameRegex = Pattern.compile(chineseNamePattern);
        Matcher chineseNameMatcher = chineseNameRegex.matcher(input);

        Pattern phoneNumberRegex = Pattern.compile(phoneNumberPattern);
        Matcher phoneNumberMatcher = phoneNumberRegex.matcher(input);

        Pattern captchaRegex=Pattern.compile(captchaPattern);
        Matcher captchaMatcher=captchaRegex.matcher(input);

        StringBuilder Input = new StringBuilder(input);
        if (chineseNameMatcher.find()) {
            int start = chineseNameMatcher.start();
            int end = chineseNameMatcher.end();
            //9 The start of the name
            Input.replace(start+9, end-1, "*");
        }
        if (phoneNumberMatcher.find()) {
            int start = phoneNumberMatcher.start();
            int end = phoneNumberMatcher.end();
            Input.replace(start+3, end-4, "****");
        }
        if (captchaMatcher.find()) {
            int start = captchaMatcher.start();
            int end = captchaMatcher.end();
            Input.replace(start+1, end-1, "****");
        }
        return Input.toString();
    }
    @After("requestMapping()")
    public void doAfter() throws Throwable {
        // 结束后打个分隔线，方便查看
        log.info("=========================================== End ===========================================");
    }
    @Around("requestMapping()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        //开始时间
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        String s = new ObjectMapper().writeValueAsString(result);

        // 打印出参
        log.info("Response Args  : {}", maskSecrets(s));
        // 执行耗时
        log.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
        return result;
    }


}
