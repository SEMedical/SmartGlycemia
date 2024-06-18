package edu.tongji.backend.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.tongji.backend.dto.*;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.mapper.UserMapper;
import edu.tongji.backend.util.Response;
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

    /**
     *
     * @param loginForm
     *          contact-Phone number,must adhere to the format that 1[3-9]xxxxxxxxx,the length is fixed and 11
     *          code-captcha code,which consists of 6 digits and if the first digit is 0,it can't be ommitted.
     *          password- unused in this api
     * @param session unused
     * @apiNote
     * <ul>
     * <li>User have 5 chances to login before successfully login,if successfully log in,the retries time limit will be reset to 5</li>
     * <li>If failed more than 5 times consecutively,the account will be frozen for 12 hours</li>
     * </ul>
     * @return ResponseEntity which consists of LoginDTO,in which there're
     * <ol>
     * <li>token(JWT)</li>
     * <li>Role(Admin/Doctor/Patient),</li>
     * <li>Name</li>
     * <li>Captcha</li>
     * </ol>
     * @since <a href="https://github.com/SEMedical/Backend/releases/tag/v1.2.0">1.2.0</a>
     * @author <a href="https://github.com/VictorHuu">Victor Hu</a>
     * @see edu.tongji.backend.controller.LoginController#sendCaptcha(String, HttpSession)
     */
    @PostMapping("/phone")
    public ResponseEntity<Response<LoginDTO>> loginByPhone(@RequestBody LoginFormDTO loginForm, HttpSession session){
        if (loginForm.getContact() == null || loginForm.getCode() == null)  //如果请求中的内容不完整
        {
            return new ResponseEntity<>(Response.fail("手机号或验证码为空"), HttpStatus.NOT_FOUND);  //返回错误信息
        }
        String contact=loginForm.getContact();
        if(!StrUtil.isNotBlank(stringRedisTemplate.opsForValue().get(LOGIN_LIMIT+contact))) {
            stringRedisTemplate.opsForValue().set(LOGIN_LIMIT + contact, String.valueOf(5));
            stringRedisTemplate.expire(LOGIN_LIMIT + contact,12, TimeUnit.HOURS);
        }else{
            stringRedisTemplate.expire(LOGIN_LIMIT + contact,12, TimeUnit.HOURS);
            if(Integer.valueOf(stringRedisTemplate.opsForValue().get(LOGIN_LIMIT+contact))<0) {
                String msg = "You've retried more than 5 times,your account will be frozen for 12 hours";
                log.error(msg);
                return new ResponseEntity<>(Response.fail(msg),HttpStatus.TOO_MANY_REQUESTS);
            }
        }
        return userService.loginByPhone(loginForm,session);
    }

    /**
     *
     * @param contact the requirement is the same as above function loginByPhone
     * @param session
     * @since <a href="https://github.com/SEMedical/Backend/releases/tag/v1.2.0">1.2.0</a>
     * @author <a href="https://github.com/VictorHuu">Victor Hu</a>
     * @return captcha code,which consists of 6 digits,and the expiration is 1 minute.
     * @see edu.tongji.backend.controller.LoginController#loginByPhone(LoginFormDTO, HttpSession) 
     */
    @RequestMapping("/captcha")
    @SentinelResource("captcha")
    public ResponseEntity<Result> sendCaptcha(@RequestBody String contact, HttpSession session){

        return userService.sendCode(contact,session);
    }

    /**
     * @apiNote Only can be called by OA Service
     * @return the maximum ids of user so far.
     * @since <a href="https://github.com/SEMedical/Backend/releases/tag/v2.0.0">2.0.0</a>
     * @author <a href="https://github.com/rmEleven">rmEleven</a>
     */
    @GetMapping("/getMaxUserId")
    public Integer getMaxUserId(){
        return userMapper.getMaxUserId();
    }
    /**
     * NOTE:only can be called by oa service
     * <p>Description:check whether the contact is available ,</p>
     * @return http status code along with a bool response:<b>true</b> for unavailability,vice versa.
     * @since <a href="https://github.com/SEMedical/Backend/releases/tag/v2.0.0">2.0.0</a>
     * @author <a href="https://github.com/rmEleven">rmEleven</a>
     */
    @GetMapping("/repeatedContact")
    public Response<Boolean> repeatedContact(@RequestParam("contact") String contact){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.select("user_id")
                .eq("contact", contact);
        User result = userMapper.selectOne(wrapper);
        if (result != null) {
            return Response.success(true, "The phone number has been registered");  // 手机号已被注册
        }
        return Response.success(false, "The phone number is available");
    }
    @GetMapping("/me")
    public Result me(){
        UserDTO user= UserHolder.getUser();
        log.info("One want to get his message:"+user);
        return Result.ok(user);
    }
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @apiNote
     * <ul>
     * <li>it's by password to login,not captcha</li>
     * <li>During frozen period,the user can't login.</li>
     * </ul>
     * @param user must consists of contact and password
     *  contact 
     *  password- must adhere to the principle that there must be at least one capital,one small letter,one special character and one digit.
     *             The length must be between 8 and 16.
     * @return
     * @throws NoSuchAlgorithmException
     * @since  <a href="https://github.com/SEMedical/Backend/releases/tag/v1.0.0">1.0.0</a>
     * @author <a href="https://github.com/a-little-dust">a-little-dust</a>,<a href="https://github.com/VictorHuu">Victor Hu</a>
     * ,<a href="https://github.com/UltraTempest10">UltraTempest10</a>
     * @see edu.tongji.backend.controller.LoginController#loginByPhone(LoginFormDTO, HttpSession) 
     * @see edu.tongji.backend.controller.RegisterController#registerPatient(RegisterDTO) 
     */
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
            stringRedisTemplate.expire(LOGIN_LIMIT + contact,12, TimeUnit.HOURS);
        }else{
            stringRedisTemplate.expire(LOGIN_LIMIT + contact,12, TimeUnit.HOURS);
            if(Integer.valueOf(stringRedisTemplate.opsForValue().get(LOGIN_LIMIT+contact))<0) {
                String msg = "You've retried more than 5 times,your account will be frozen for 12 hours";
                log.error(msg);
                return new ResponseEntity<>(Response.fail(msg),HttpStatus.TOO_MANY_REQUESTS);
            }
        }
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
    @GetMapping("/getContactForAdmin")
    String getContactForAdmin(@RequestParam("userId") String userId){
        String contact=userService.getContact(userId);
        return contact;
    }
    @PostMapping("/sign")
    public ResponseEntity<Response<Integer>> sign(){
        UserDTO user= UserHolder.getUser();
        return userService.sign(user);
    }
    @GetMapping("/sign/count")
    public ResponseEntity<Response<Integer>> signCount(){
        UserDTO user= UserHolder.getUser();
       return userService.signCount(user);
    }
    @PostMapping("/updateAdminInfo")
    public Boolean updateAdmin(@RequestBody AdminDTO admin){
        return userService.updateAdminInfo(admin.getAdminId(),admin.getName(),admin.getContact());
    }
}
