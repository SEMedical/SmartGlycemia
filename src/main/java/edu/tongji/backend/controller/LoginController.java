package edu.tongji.backend.controller;

import edu.tongji.backend.util.Response;
import edu.tongji.backend.dto.LoginDTO;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController  //用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/login")  //对应的api路径
public class LoginController {
    @Autowired  //自动装填接口的实现类
    IUserService userService;

    @PostMapping//对应的api路径
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

        return Response.success(loginDTO,"登录成功");  //返回成功信息
    }
}
