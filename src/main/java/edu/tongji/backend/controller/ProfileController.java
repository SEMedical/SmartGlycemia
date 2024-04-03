package edu.tongji.backend.controller;

import edu.tongji.backend.dto.ProfileDTO;
import edu.tongji.backend.dto.UserDTO;
import edu.tongji.backend.entity.Profile;
import edu.tongji.backend.service.IProfileService;
import edu.tongji.backend.service.IUserService;
import edu.tongji.backend.util.Jwt;
import edu.tongji.backend.util.Response;
import edu.tongji.backend.util.UserHolder;
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
        UserDTO user= UserHolder.getUser();
        Integer userId= Integer.valueOf(user.getUserId());
        ProfileDTO profileDTO = profileService.getCompleteProfile(userId);
        if (profileDTO == null) {
            return Response.fail("查询健康档案失败");
        }
        return Response.success(profileDTO, "查询健康档案成功");
    }

    @PostMapping("/update-health-record") //对应的api路径
    public Response<Boolean> updateHealthRecord(HttpServletRequest request, @RequestBody ProfileDTO profileDTO) throws ParseException {
        UserDTO user= UserHolder.getUser();
        Integer userId= Integer.valueOf(user.getUserId());

        System.out.println(profileDTO);

        if (profileService.updateProfile(userId, profileDTO)) {
            return Response.success(true, "更新健康档案成功");
        } else {
            return Response.fail("更新健康档案失败");
        }
    }

    @GetMapping("/getUserName")
    public Response<String> test(HttpServletRequest request){
        UserDTO user= UserHolder.getUser();
        Integer userId= Integer.valueOf(user.getUserId());
        String name=profileService.getUserName(userId);
        if(name==null){
            return Response.fail("获取用户名失败");
        }
        return Response.success(name,"获取用户名成功");
    }
}

