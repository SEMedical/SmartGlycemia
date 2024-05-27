package edu.tongji.backend.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.tongji.backend.dto.LoginFormDTO;
import edu.tongji.backend.dto.Result;
import edu.tongji.backend.dto.UserDTO;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.mapper.UserMapper;
import edu.tongji.backend.util.Response;
import edu.tongji.backend.dto.LoginDTO;
import edu.tongji.backend.service.IUserService;
import edu.tongji.backend.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import static edu.tongji.backend.util.RedisConstants.LOGIN_LIMIT;

@Slf4j
@RestController  //用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/api/login")  //对应的api路径
public class LoginController {
    @Autowired  //自动装填接口的实现类
    IUserService userService;
    @Autowired
    UserMapper userMapper;
    @PostMapping("/phone")
    public ResponseEntity<Response<LoginDTO>> loginByPhone(@RequestBody LoginFormDTO loginForm, HttpSession session){
        return userService.loginByPhone(loginForm,session);
    }
    @RequestMapping("/captcha")
    @SentinelResource("captcha")
    public Result sendCaptcha(@RequestBody String contact, HttpSession session){
        return userService.sendCode(contact,session);
    }
    @GetMapping("/getMaxUserId")
    public Integer getMaxUserId(){
        return userMapper.getMaxUserId();
    }
    /**
     * NOTE:only can be called by oa service
     * <p>Description:check whether the contact is available ,</p>
     * @return http status code along with a bool response:<b>true</b> for unavailability,vice versa.
     * @since 2.2.0
     * @author <a href="https://github.com/VictorHuu">Victor Hu</a>
     */
    @GetMapping("/repeatedContact")
    public Response<Boolean> repeatedContact(@RequestParam("contact") String contact){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.select("user_id")
                .eq("contact", contact);
        User result = userMapper.selectOne(wrapper);
        try {
            if (result != null) {
                return Response.success(true, "The phone number has been registered");  // 手机号已被注册
            }
            return Response.success(false, "The phone number is available");
        }catch (Exception e){
            e.printStackTrace();
        }
        return Response.fail("Service 's crashed down!");
    }
    @GetMapping("/me")
    public Result me(){
        UserDTO user= UserHolder.getUser();
        return Result.ok(user);
    }
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @PostMapping("/pass")//对应的api路径
    public ResponseEntity<Response<LoginDTO>> login(@RequestBody User user) throws NoSuchAlgorithmException  //把请求中的内容映射到user
    {
        
        if (user.getContact() == null || user.getPassword() == null)  //如果请求中的内容不完整
        {
            return new ResponseEntity<>(Response.fail("手机号或密码为空"), HttpStatus.NOT_FOUND);  //返回错误信息
        }
        String contact=user.getContact();
        if(!StrUtil.isNotBlank(stringRedisTemplate.opsForValue().get(LOGIN_LIMIT+contact))) {
            stringRedisTemplate.opsForValue().set(LOGIN_LIMIT + contact, String.valueOf(5));
        }else{
            if(Integer.valueOf(stringRedisTemplate.opsForValue().get(LOGIN_LIMIT+contact))<0) {
                String msg = "You've retried more than 5 times,your account will be frozen for 12 hours";
                log.error(msg);
                return new ResponseEntity<>(Response.fail(msg),HttpStatus.TOO_MANY_REQUESTS);
            }
        }
        stringRedisTemplate.expire(LOGIN_LIMIT + contact,12, TimeUnit.HOURS);
        LoginDTO loginDTO = userService.login(user.getContact(), user.getPassword());  //调用接口的login函数
        if (loginDTO == null)  //如果返回的loginDTO为空
        {
            stringRedisTemplate.opsForValue().decrement(LOGIN_LIMIT+contact);
            Integer i = Integer.valueOf(stringRedisTemplate.opsForValue().get(LOGIN_LIMIT+contact));
            stringRedisTemplate.opsForValue().set(LOGIN_LIMIT+contact,String.valueOf(i-1));
            String msg="You can only try no more than"+ String.valueOf(i)+" times";
            log.warn(msg);
            return new ResponseEntity<>(Response.fail(msg),HttpStatus.BAD_REQUEST);  //返回错误信息
        }
        log.info("登录成功");
        stringRedisTemplate.delete(LOGIN_LIMIT+contact);
        return new ResponseEntity<>(Response.success(loginDTO,"登录成功"),HttpStatus.OK);  //返回成功信息
    }
    @PostMapping("/sign")
    public Result sign(){
        UserDTO user= UserHolder.getUser();
        return userService.sign(user);
    }
    @PostMapping("sign/count")
    public Result signCount(){
        UserDTO user= UserHolder.getUser();
       return userService.signCount(user);
    }
}
