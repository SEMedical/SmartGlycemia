package edu.tongji.backend.controller;

import edu.tongji.backend.dto.LoginFormDTO;
import edu.tongji.backend.dto.Result;
import edu.tongji.backend.dto.UserDTO;
import edu.tongji.backend.service.IExerciseService;
import edu.tongji.backend.service.impl.UserServiceImpl;
import edu.tongji.backend.util.Response;
import edu.tongji.backend.dto.LoginDTO;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.service.IUserService;
import edu.tongji.backend.util.UserHolder;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController  //用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/api/login")  //对应的api路径
public class LoginController {
    @Autowired  //自动装填接口的实现类
    IUserService userService;
    @Autowired
    IExerciseService exerciseService;
    @PostMapping("/phone")
    public Result loginByPhone(@RequestBody LoginFormDTO loginForm, HttpSession session){
        return userService.loginByPhone(loginForm,session);
    }
    @PostMapping("/captcha")
    public Result sendCaptcha(@RequestBody String contact, HttpSession session){
        return userService.sendCode(contact,session);
    }
    @GetMapping("/me")
    public Result me(){
        UserDTO user= UserHolder.getUser();
        return Result.ok();
    }
    @PostMapping("/pass")//对应的api路径
    public Response<LoginDTO> login(@RequestBody User user)  //把请求中的内容映射到user
    {
        if (user.getContact() == null || user.getPassword() == null)  //如果请求中的内容不完整
        {
            return Response.fail("手机号或密码为空");  //返回错误信息
        }

        LoginDTO loginDTO = userService.login(user.getContact(), user.getPassword());  //调用接口的login函数
        if (loginDTO == null)  //如果返回的loginDTO为空
        {
            return Response.fail("账号或密码不正确");  //返回错误信息
        }
        System.out.println("登录成功");
        exerciseService.finishExercise(userService.getUserId(user.getContact()).toString());
        return Response.success(loginDTO,"登录成功");  //返回成功信息
    }
    @PostMapping("/sign")
    public Result sign(){
        UserDTO user= UserHolder.getUser();
        return userService.sign(user);
    }
}
