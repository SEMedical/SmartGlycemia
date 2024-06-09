package edu.tongji.backend.util;

import edu.tongji.backend.dto.UserDTO;
import edu.tongji.backend.entity.User;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static edu.tongji.backend.util.RedisConstants.LOGIN_TOKEN_KEY;
import static edu.tongji.backend.util.RedisConstants.LOGIN_TOKEN_TTL;

public class LoginInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //TODO get token in the header
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        if(UserHolder.getUser()==null) {
            response.setStatus(401);
            return false;
        }
        if(!UserHolder.getUser().getRole().equals("patient")) {
            response.setStatus(418);
            PrintWriter out = response.getWriter();
            String message="Only administrator account can access this method!";
            String success="false";
            String jsonResponse = "{ \"message\": \"" + message + "\", \"success\": \"" + success + "\" }";
            out.write(jsonResponse);
            out.flush();
            return false;
        }
        return true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
