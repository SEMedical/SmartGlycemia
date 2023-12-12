package edu.tongji.backend.controller;

import edu.tongji.backend.entity.user;
import edu.tongji.backend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController//用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/login")//对应的api路径
public class LoginController {
    @Autowired//自动装填接口的实现类
    IUserService userService;
    @PostMapping//对应的api路径
    public Boolean insert(@RequestBody user user)//把请求中的内容映射到user
    {
        return userService.login(user.getUser_id(),user.getPassword());//调用接口的login函数
    }
}
