package edu.tongji.backend.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import edu.tongji.backend.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static edu.tongji.backend.util.RedisConstants.LOGIN_TOKEN_KEY;
import static edu.tongji.backend.util.RedisConstants.LOGIN_TOKEN_TTL;
@Slf4j
public class RefreshTokenInterceptor implements HandlerInterceptor {
    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //TODO get token in the header
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        String token = request.getHeader("Authorization");
        if(StrUtil.isBlank(token)){
            return true;
        }
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(LOGIN_TOKEN_KEY+token);
        if(userMap.isEmpty()){
            //Doesn't exist
            return true;
        }
        //TODO convert Hash to UserDTO
        UserDTO userDTO= BeanUtil.fillBeanWithMap(userMap,new UserDTO(),false);
        //Exist(ThreadLocal)
        UserHolder.saveUser(userDTO);
        //TODO Refresh expiration of the token
        stringRedisTemplate.expire(LOGIN_TOKEN_KEY+token,LOGIN_TOKEN_TTL, TimeUnit.MINUTES);

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
