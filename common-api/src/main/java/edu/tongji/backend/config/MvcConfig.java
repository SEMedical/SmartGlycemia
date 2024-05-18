package edu.tongji.backend.config;

import edu.tongji.backend.util.LoginInterceptor;
import edu.tongji.backend.util.RefreshTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        //Login
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns("error","/api/login/captcha","/api/login/phone","/api/register/doctor",
                "/api/register/patient","/api/login/pass","/api/oa/*","/api/register/addUser")
                .order(1);
        //Token Refresh
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).
                excludePathPatterns("error","/api/login/captcha","/api/login/phone","/api/register/doctor",
                        "/api/register/patient","/api/login/pass","/api/oa/*","/api/register/addUser")
                .order(0);
    }
}
