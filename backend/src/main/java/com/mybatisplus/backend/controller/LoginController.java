package com.mybatisplus.backend.controller;

import com.mybatisplus.backend.entity.user;
import com.mybatisplus.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController//用于把后端数据流返回给前端
public class LoginController {
    @Autowired//自动装填
    UserMapper userMapper;
    @RequestMapping("/insert")//对应的api路径
    public String insert(Integer user_id,String name,Integer age,String contact,String password,String role){
        return userMapper.insert(new user(user_id,age,name,contact,password,role))>0?"Success!":"fail";
    }
}
