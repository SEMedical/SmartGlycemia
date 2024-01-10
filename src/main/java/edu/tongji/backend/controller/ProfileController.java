package edu.tongji.backend.controller;

import edu.tongji.backend.dto.ProfileDTO;
import edu.tongji.backend.entity.Profile;
import edu.tongji.backend.service.IProfileService;
import edu.tongji.backend.service.IUserService;
import edu.tongji.backend.util.Jwt;
import edu.tongji.backend.util.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController//用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/api/health")//对应的api路径
public class ProfileController {
    @Autowired
    IUserService userService;

    @Autowired
    IProfileService profileService;

    @GetMapping("/health-record") //对应的api路径
    public Response<ProfileDTO> getHealthRecord(HttpServletRequest request) throws ParseException {
        if (request.getHeader("Authorization") == null) {
            return Response.fail("您尚未登录");
        }
        String token = request.getHeader( "Authorization");
//        System.out.println(token);
        Integer userId = (Integer) Jwt.parse(token).get("userId");
        String role = (String) Jwt.parse(token).get("userPermission");
        if (userService.getById(userId) == null) {
            return Response.fail("用户不存在");
        }
        if (!role.equals("patient")) {
            return Response.fail("用户不是病人");
        }

        ProfileDTO profileDTO = profileService.getCompleteProfile(userId);
        if (profileDTO == null) {
            return Response.fail("查询健康档案失败");
        }
        return Response.success(profileDTO, "查询健康档案成功");
    }

    @PostMapping("/update-health-record") //对应的api路径
    public Response<Boolean> updateHealthRecord(HttpServletRequest request, @RequestBody ProfileDTO profileDTO) throws ParseException {
        if (request.getHeader("Authorization") == null) {
            return Response.fail("您尚未登录");
        }
        String token = request.getHeader("Authorization");
//        System.out.println(token);
        Integer userId = (Integer) Jwt.parse(token).get("userId");
        String role = (String) Jwt.parse(token).get("userPermission");
        if (userService.getById(userId) == null) {
            return Response.fail("用户不存在");
        }
        if (!role.equals("patient")) {
            return Response.fail("用户不是病人");
        }

        System.out.println(profileDTO);

        if (profileService.updateProfile(userId, profileDTO)) {
            return Response.success(true, "更新健康档案成功");
        } else {
            return Response.fail("更新健康档案失败");
        }
    }

    @GetMapping("/getUserName")
    public Response<String> test(HttpServletRequest request){
            if (request.getHeader("Authorization") == null) {
                return Response.fail("您尚未登录");
            }
        String token = request.getHeader( "Authorization");
        Integer userId = (Integer) Jwt.parse(token).get("userId");
        String role = (String) Jwt.parse(token).get("userPermission");
        if (userService.getById(userId) == null) {
            return Response.fail("用户不存在");
        }
        if (!role.equals("patient")) {
            return Response.fail("用户不是病人");
        }
String name=profileService.getUserName(userId);
        if(name==null){
            return Response.fail("获取用户名失败");
        }
        return Response.success(name,"获取用户名成功");
    }
}

