package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.util.JwtUtils;
import edu.tongji.backend.dto.LoginDTO;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.mapper.UserMapper;
import edu.tongji.backend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    UserMapper userMapper;
    @Override
    public LoginDTO login(Integer user_id, String password){
//        System.out.println(userMapper);
        LoginDTO loginDTO = new LoginDTO();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.select("user_id", "role")
                .eq("user_id", user_id)
                .eq("password", password);
        var result = userMapper.selectOne(wrapper);
//        System.out.println("user: " + result);

        if(result == null){
            return null;
        }

        Map<String,Object> jwtInfo = new HashMap<>();
        jwtInfo.put("userId", result.getUserId());
        jwtInfo.put("userPermission", result.getRole());
        String jwt = JwtUtils.generateJwt(jwtInfo);

        loginDTO.setToken(jwt);
        loginDTO.setRole(result.getRole());

        System.out.println("loginDTO: " + loginDTO);

        return loginDTO;
    }

    @Override
    public Integer register(String name, String password, Integer age, String contact){
        User user = new User();
//        user.setAge(age);
        return userMapper.insert(new User(0,169,"Shanghai", name, contact, password, "doctor"));
    }
}
