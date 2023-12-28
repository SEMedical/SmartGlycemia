package edu.tongji.backend.controller;

import edu.tongji.backend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import edu.tongji.backend.dto.RegisterDTO;
import edu.tongji.backend.util.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController  //用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/register")  //对应的api路径
public class RegisterController {
    @Autowired  //自动装填接口的实现类
    IUserService userService;

    @PostMapping("/patient")  //对应的api路径
    public Response<Boolean> registerPatient(@RequestBody RegisterDTO info)
    {
//        System.out.println(info);
        if (info.getContact() == null || info.getPassword() == null)  //如果请求中的内容不完整
        {
            return Response.fail("手机号或密码为空");  //返回错误信息
        }
        Integer result = userService.register(info.getName(), info.getPassword(), info.getContact(), info.getGender(), info.getAge());  //调用接口的register函数
        if (result == -1)  //如果返回的result为false
        {
            return Response.fail("手机号已被注册");  //返回错误信息
        }
        else if (result == 0)
        {
            return Response.fail("注册失败");  //返回错误信息
        }
        return Response.success(true, "注册成功");
    }

    @PostMapping("/doctor")  //对应的api路径
    public Response<Boolean> registerDoctor(@RequestBody RegisterDTO info)
    {
//        System.out.println(info);
        if (info.getContact() == null || info.getPassword() == null)  //如果请求中的内容不完整
        {
            return Response.fail("手机号或密码为空");  //返回错误信息
        }
        Integer result = userService.register(info.getName(), info.getPassword(), info.getContact());  //调用接口的register函数
        if (result == -1)  //如果返回的result为false
        {
            return Response.fail("手机号已被注册");  //返回错误信息
        }
        else if (result == 0)
        {
            return Response.fail("注册失败");  //返回错误信息
        }
        return Response.success(true, "注册成功");
    }
}
