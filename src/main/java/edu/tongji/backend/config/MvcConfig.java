package edu.tongji.backend.config;

import edu.tongji.backend.util.LoginInterceptor;
import edu.tongji.backend.util.RefreshTokenInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        //Login
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns("/api/login/captcha","/api/login/phone").order(1);
        //Token Refresh
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).excludePathPatterns("/api/login/captcha","/api/login/phone")
                .order(0);
    }
}
