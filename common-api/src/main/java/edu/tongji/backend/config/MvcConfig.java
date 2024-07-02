package edu.tongji.backend.config;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 All contributors of the project
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





import edu.tongji.backend.util.AdminInterceptor;
import edu.tongji.backend.util.DoctorInterceptor;
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
        registry.addInterceptor(new DoctorInterceptor()).
                addPathPatterns("/api/glycemia/doctor/*","/api/health/doctor/*","/api/interaction/*","/api/sports/doctor/*","/api/login/updateAdminInfo")
                .order(1);
        //Admin Login
        registry.addInterceptor(new AdminInterceptor())
                .excludePathPatterns("/error","/api/login/*","/api/register/*","/api/interaction/*","/api/health/*","/api/login/sign/*",
                        "/api/glycemia/*","/api/exercise/*","/api/sports/*","/api/login/getMaxUserId","/api/oa/register",
                        "/api/register/refresh",
                        "/api/interaction/*","/api/interaction/patient/*",
                        "/api/health/doctor/*","/api/glycemia/doctor/*","/api/sports/doctor/*",
                        "/api/login/getContactForAdmin","/api/login/updateAdminInfo","/api/register/registerHelper")
                .order(1);
        //Login
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns("/error","/api/login/captcha","/api/login/phone","/api/register/doctor",
                "/api/register/patient","/api/login/pass","/api/oa/*","/api/register/addUser","/api/register/rmUser",
                "/api/interaction/*","/api/login/logout",
                "/api/health/doctor/*","/api/glycemia/doctor/*","/api/sports/doctor/*",
                "/api/login/repeatedContact","/api/login/getMaxUserId"
                        ,"/api/register/refresh","/api/login/getContactForAdmin"
                        ,"/api/login/updateAdminInfo","/api/register/registerHelper")
                .order(1);
        //Token Refresh
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).
                excludePathPatterns("/error","/api/login/captcha","/api/login/phone","/api/register/doctor",
                        "/api/register/patient","/api/login/pass","/api/register/addUser","/api/register/rmUser",
                        "/api/login/repeatedContact","/api/login/getMaxUserId",
                        "/api/register/refresh","/api/login/getContactForAdmin"
                        ,"/api/login/updateAdminInfo","/api/oa/register","/api/register/registerHelper")
                .order(0);
    }
}
