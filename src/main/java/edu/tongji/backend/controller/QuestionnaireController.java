package edu.tongji.backend.controller;

import edu.tongji.backend.dto.Answer1DTO;
import edu.tongji.backend.dto.Answer2DTO;
import edu.tongji.backend.dto.QuestionnaireDTO;
import edu.tongji.backend.dto.SportPlanDTO;
import edu.tongji.backend.service.IProfileService;
import edu.tongji.backend.service.IQuestionnaireService;
import edu.tongji.backend.service.IUserService;
import edu.tongji.backend.util.Jwt;
import edu.tongji.backend.util.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController//用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/sports/questionnaire")//对应的api路径
public class QuestionnaireController {
    @Autowired
    IUserService userService;
    @Autowired
    IProfileService profileService;
    @Autowired
    IQuestionnaireService questionnaireService;

    @GetMapping("/1") //对应的api路径
    public Response<List<String>> getQuestionnaire1(HttpServletRequest request)
    {
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

//        System.out.println(questionnaireService.getQuestionnaire1());

        return Response.success(questionnaireService.getQuestionnaire1(), "success");
    }

    @PostMapping("/2") //对应的api路径
    public Response<List<QuestionnaireDTO>> getQuestionnaire2(HttpServletRequest request, @RequestBody Answer1DTO answer)
    {
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

//        System.out.println(answer);

//        System.out.println(questionnaireService.getQuestionnaire2(userId, answer.getResult()));

        List<QuestionnaireDTO> result = questionnaireService.getQuestionnaire2(userId, answer.getResult());
        if (result == null) {
            return Response.fail("问卷上传失败");
        }

        return Response.success(result, "success");
    }

    @PostMapping("/recommended-sport-plan") //对应的api路径
    public Response<SportPlanDTO> getRecommendedSportPlan(HttpServletRequest request, @RequestBody Answer2DTO answer)
    {
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

        System.out.println(answer);

        SportPlanDTO result = questionnaireService.getRecommendedSportPlan(userId, answer.getResult());
        if (result == null) {
            return Response.fail("问卷上传失败");
        }

//        System.out.println(profileService.getRecommendedSportPlan(userId));

//        return Response.success(profileService.getRecommendedSportPlan(userId), "success");
        return Response.success(result, "success");
    }
}
