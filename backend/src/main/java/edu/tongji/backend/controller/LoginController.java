package edu.tongji.backend.controller;

import edu.tongji.backend.entity.user;
import edu.tongji.backend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")//对应的api路径
public class LoginController {
    @Autowired//自动装填
    IUserService userService;
    @PostMapping//对应的api路径
    public Boolean insert(@RequestBody user user){
        return userService.login(user.getUser_id(),user.getPassword());
    }
}
