package edu.tongji.backend.config;

import edu.tongji.backend.util.TokenHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Configuration
public class FeignConfig implements RequestInterceptor {
    //TODO:ThreadLocal
    public static String getToken() {
        String token = TokenHolder.getToken();
        return token;
    }
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("authorization", getToken());
    }
}
