package edu.tongji.backend.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.dto.LoginFormDTO;
import edu.tongji.backend.dto.Result;
import edu.tongji.backend.dto.UserDTO;
import edu.tongji.backend.entity.Profile;
import edu.tongji.backend.mapper.ProfileMapper;
import edu.tongji.backend.util.Jwt;
import edu.tongji.backend.dto.LoginDTO;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.mapper.UserMapper;
import edu.tongji.backend.service.IUserService;
import edu.tongji.backend.util.RegexUtils;
import edu.tongji.backend.util.Response;
import edu.tongji.backend.util.UserHolder;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static edu.tongji.backend.util.RedisConstants.*;
import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    ProfileMapper profileMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private static List<Integer> getRandomNumber(int count) {
        // 使用SET以此保证写入的数据不重复
        List<Integer> set = new ArrayList<Integer>();
        // 随机数
        Random random = new Random();

        while (set.size() < count) {
            // nextInt返回一个伪随机数，它是取自此随机数生成器序列的、在 0（包括）
            // 和指定值（不包括）之间均匀分布的 int 值。
            set.add(random.nextInt(10));
        }
        return set;
    }
    private static String generatedcode(int count) {
        List<Integer> set = getRandomNumber(count);
        // 使用迭代器
        Iterator<Integer> iterator = set.iterator();
        // 临时记录数据
        String temp = "";
        while (iterator.hasNext()) {
            temp += iterator.next();

        }
        return temp;
    }
    Result createUserWithPhone(String contact,QueryWrapper<User> wrapper){
        User user = new User();
        user.setContact(contact);
        user.setName("momo");
        user.setRole("patient");
        int userNum = userMapper.insert(user);
        User result = userMapper.selectOne(wrapper);
        Profile profile = new Profile();
        profile.setPatientId(result.getUserId());
        log.info("The UID:"+result.getUserId());
        int profileNum = profileMapper.insert(profile);
        log.info("userNum: " + userNum + ", profileNum: " + profileNum);
        if(userNum==1&&profileNum==0)
            throw new RuntimeException("Register failed,rollback!");
        return userNum == 1 && profileNum == 1 ? Result.ok() : Result.fail("Create user failed");
    }
    @Override
    public Response<LoginDTO> loginByPhone(@RequestBody LoginFormDTO loginForm, HttpSession session){
        //1. Check phone and verification
        String contact=loginForm.getContact();
        if(RegexUtils.isPhoneInvaild(contact)) {
            //. return error msg
            log.warn("Wrong format of contact");
            return Response.fail("Wrong format of contact");
        }
        //2. error TODO :get captcha from Redis
        String cachecode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY+contact);
        String code=loginForm.getCode();
        if(cachecode==null||!cachecode.equals(code)){
            return Response.fail("verification failed");
        }
        //3. find user by phonenumber
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.select("user_id", "role", "name")
                .eq("contact", contact);
        User userinfo = userMapper.selectOne(wrapper);
        //4. user exists
        if(userinfo==null){
            createUserWithPhone(contact, wrapper);
        }
        // TODO save userinfo to Redis
        // TODO generate token,as login pass
        Map<String,Object> jwtInfo = new HashMap<>();
        jwtInfo.put("userId", userinfo.getUserId());
        jwtInfo.put("userPermission", userinfo.getRole());
        String token = Jwt.generate(jwtInfo);
        UserDTO userDTO= BeanUtil.copyProperties(userinfo,UserDTO.class);
        Map<String,Object> userMap=BeanUtil.beanToMap(userDTO,new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true).setFieldValueEditor(
                        (fieldName,fieldValue)->fieldValue!=null?fieldValue.toString():"NULL"
                ));
        stringRedisTemplate.opsForHash().putAll(LOGIN_TOKEN_KEY+token,userMap);
        stringRedisTemplate.expire(LOGIN_TOKEN_KEY+token,LOGIN_TOKEN_TTL,TimeUnit.MINUTES);
        //6. save userinfo to sessions
        session.setAttribute("user", BeanUtil.copyProperties(userinfo,UserDTO.class));
        session.setAttribute("authorization",token);
        //No need to return JWT,because it's carried by session
        LoginDTO loginDTO=new LoginDTO(token, userinfo.getRole(), userinfo.getName(),code);
        return Response.success(loginDTO,"Login Success");
    }
    @Override
    public Result sendCode(String contact, HttpSession session){
        //. Check Phone
        if(RegexUtils.isPhoneInvaild(contact)) {
            //. return error msg
            return Result.fail("Wrong format of contact");
        }
        //. fit,generate verification code
        String code= generatedcode(6);
        //.save captcha to the session TODO redis
        //session.setAttribute("code",code);
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY+contact,code,LOGIN_CODE_TIMEOUT, TimeUnit.MINUTES);

        //. send verification(Ali-Cloud)
        log.debug("send verification code successfully,captcha: {}",code);
        session.setAttribute("Captcha",code);
        //return OK
        return Result.ok("The code is "+code);
    }
    @Override
    public LoginDTO login(String contact, String password){
//        log.info(userMapper);
        LoginDTO loginDTO = new LoginDTO();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.select("user_id", "role", "name")
                .eq("contact", contact)
                .eq("password", password);
        User result = userMapper.selectOne(wrapper);
//        log.info("user: " + result);

        if(result == null){
            return null;
        }

        Map<String,Object> jwtInfo = new HashMap<>();
        jwtInfo.put("userId", result.getUserId());
        jwtInfo.put("userPermission", result.getRole());
        String jwt = Jwt.generate(jwtInfo);
        loginDTO.setToken(jwt);
        loginDTO.setRole(result.getRole());
        loginDTO.setName(result.getName());
        UserDTO userDTO= BeanUtil.copyProperties(result,UserDTO.class);
        Map<String,Object> userMap=BeanUtil.beanToMap(userDTO,new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true).setFieldValueEditor(
                        (fieldName,fieldValue)->fieldValue!=null?fieldValue.toString():"NULL"
                ));
        stringRedisTemplate.opsForHash().putAll(LOGIN_TOKEN_KEY+jwt,userMap);
        stringRedisTemplate.expire(LOGIN_TOKEN_KEY+jwt,LOGIN_TOKEN_TTL,TimeUnit.MINUTES);
//        log.info("loginDTO: " + loginDTO);

        return loginDTO;
    }

    // 病人注册
    @Override
    @Transactional
    public Integer register(String name, String password, String contact, String gender, Integer age){
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.select("user_id")
                    .eq("contact", contact);
            User result = userMapper.selectOne(wrapper);
            if (result != null) {
                return -1;  // 手机号已被注册
            }

            User user = new User();
            user.setName(name);
            user.setContact(contact);
            user.setPassword(password);
            user.setRole("patient");
            int userNum = userMapper.insert(user);
            result = userMapper.selectOne(wrapper);
            Profile profile = new Profile();
            profile.setPatientId(result.getUserId());
            log.info("The UID:"+result.getUserId());
            profile.setGender(gender);
            profile.setAge(age);
            int profileNum = profileMapper.insert(profile);
            log.info("userNum: " + userNum + ", profileNum: " + profileNum);
            if(userNum==1&&profileNum==0)
                throw new RuntimeException("Register failed,rollback!");
            return userNum == 1 && profileNum == 1 ? 1 : 0;

    }

    // 医生注册
    @Override
    public Integer register(String name, String password, String contact){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.select("user_id")
                .eq("contact", contact);
        User result = userMapper.selectOne(wrapper);
        if(result != null){
            return -1;  // 手机号已被注册
        }

        User user = new User();
        user.setName(name);
        user.setContact(contact);
        user.setPassword(password);
        user.setRole("doctor");
        int userNum = userMapper.insert(user);

        return userNum == 1 ? 1 : 0;
    }
    @Override
    public Integer getUserId(String contact)
    {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("contact", contact);
        User user = userMapper.selectOne(wrapper);
        return user.getUserId();
    }

    @Override
    public Result sign(UserDTO user) {
        String userId = user.getUserId();
        //get date
        LocalDateTime now= LocalDateTime.now();
        //concat key
        String keySuffix = now.format(DateTimeFormatter.ofPattern("yyyy:MM"));
        log.info(keySuffix);
        String key=USER_SIGN_KEY+userId+":"+keySuffix;
        //get the day of month
        int dayOfMonth=now.getDayOfMonth();
        //write to the Redis
        stringRedisTemplate.opsForValue().
                setBit(key,dayOfMonth-1,true);
        return Result.ok();
    }

    @Override
    public Result signCount(UserDTO user) {
        String userId = user.getUserId();
        //get date
        LocalDateTime now= LocalDateTime.now();
        //concat key
        String keySuffix = now.format(DateTimeFormatter.ofPattern("yyyy:MM"));
        String key=USER_SIGN_KEY+userId+keySuffix;
        int dayOfMonth=now.getDayOfMonth();
        //5. get all the sign records
        List<Long> result = stringRedisTemplate.opsForValue().bitField(
                key, BitFieldSubCommands.create().get(
                        BitFieldSubCommands.BitFieldType.unsigned(
                                dayOfMonth
                        )
                ).valueAt(0)
        );//Because there might be many subcommands ,so the return type is list
        if(result==null||result.isEmpty())
            return  Result.ok(0);
        Long num=result.get(0);
        if(num==null||num==0)
            return Result.ok(0);
        int count=0;
        //6. Iterate
        while (true){
            //7. bitwise op with 1
            if((num&1)==0){
                break;
            }else{
                count++;
            }
            num>>=1;//TODO:>>>???(unsigned right shift)
        }
        return Result.ok(count);
    }

    @Override
    public void addUser(User user) {
        try {
            userMapper.insert(user);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void rmUser(Integer userId) {
        try {
            userMapper.deleteById(userId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
