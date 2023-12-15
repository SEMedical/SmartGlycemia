package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.mapper.UserMapper;
import edu.tongji.backend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    UserMapper userMapper;
    @Override
    public boolean login(Integer id, String password){
        System.out.println(userMapper);
        Boolean result = userMapper.selectById(id).getPassword().equals(password);
        System.out.println("Password"+(result?"Correct":"Wrong"));
        return result;
    }

    @Override
    public Integer register(String name, String password,Integer age,String contact){
        Integer id = userMapper.insert(new User(0,169,"Shanghai", name, contact, password, "doctor"));
        return id;
    }
}
