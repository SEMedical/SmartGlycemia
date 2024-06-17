package edu.tongji.backend.controller;

import edu.tongji.backend.dto.ProfileDTO;
import edu.tongji.backend.dto.UserDTO;
import edu.tongji.backend.entity.Profile;
import edu.tongji.backend.service.IProfileService;
import edu.tongji.backend.service.IUserService;
import edu.tongji.backend.util.Jwt;
import edu.tongji.backend.util.Response;
import edu.tongji.backend.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
@Slf4j
@RestController//用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/api/health")//对应的api路径
public class ProfileController {
    @Autowired
    IUserService userService;

    @Autowired
    IProfileService profileService;

    @GetMapping("/health-record") //对应的api路径
    public Response<ProfileDTO> getHealthRecord() throws ParseException {
        UserDTO user= UserHolder.getUser();
        Integer userId= Integer.valueOf(user.getUserId());
        ProfileDTO profileDTO = profileService.getCompleteProfile(userId);
//        if (profileDTO == null) {
//            return Response.fail("查询健康档案失败");
//        }
        return Response.success(profileDTO, "查询健康档案成功");
    }

    /**
     * @apiNote only for Enterprise Version
     * @param patient_id For doctor to look up the health record of patient
     * @throws ParseException
     */
    @GetMapping("/doctor/health-record") //对应的api路径
    public Response<ProfileDTO> getHealthRecordForDoctor(Integer patient_id) throws ParseException {
        ProfileDTO profileDTO = profileService.getCompleteProfile(patient_id);
//        if (profileDTO == null) {
//            return Response.fail("查询健康档案失败");
//        }
        return Response.success(profileDTO, "查询健康档案成功");
    }
    @PostMapping("/update-health-record") //对应的api路径
    public Response<Boolean> updateHealthRecord(@RequestBody ProfileDTO profileDTO) throws ParseException {
        UserDTO user= UserHolder.getUser();
        Integer userId= Integer.valueOf(user.getUserId());

        log.info(profileDTO.toString());

        Boolean flag=profileService.updateProfile(userId, profileDTO);
        return Response.success(flag, "更新健康档案成功");
    }
    //RPC for glycemia-service
    @GetMapping("/getUserAge")
    public Response<Integer> getUserAge(){
        UserDTO user= UserHolder.getUser();
        Integer userId= Integer.valueOf(user.getUserId());
        Integer age=profileService.getUserAge(userId);
        if(age==null){
            return Response.fail("获取用户年龄失败");
        }
        return Response.success(age,"获取用户年龄成功");
    }
    @GetMapping("/getProfile")
    Profile getByPatientId(HttpServletRequest request){
        UserDTO user= UserHolder.getUser();
        String patient_id= user.getUserId();
        return profileService.getByPatientId(patient_id);
    }
    @GetMapping("/getUserName")
    public Response<String> getUserName(HttpServletRequest request){
        UserDTO user= UserHolder.getUser();
        Integer userId=Integer.valueOf(user.getUserId());
        String name=profileService.getUserName(userId);
        if(name==null){
            return Response.fail("获取用户名失败");
        }
        return Response.success(name,"获取用户名成功");
    }
}

