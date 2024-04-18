package edu.tongji.backend.controller;

import edu.tongji.backend.dto.*;
import edu.tongji.backend.service.IQuestionnaireService;
import edu.tongji.backend.util.Response;
import edu.tongji.backend.util.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController//用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/api/sports/questionnaire")//对应的api路径
public class QuestionnaireController {
    @Autowired
    IQuestionnaireService questionnaireService;

    @GetMapping("/1") //对应的api路径
    public Response<List<String>> getQuestionnaire1(HttpServletRequest request)
    {
        UserDTO user= UserHolder.getUser();
        Integer userId= Integer.valueOf(user.getUserId());

//        System.out.println(questionnaireService.getQuestionnaire1());

        return Response.success(questionnaireService.getQuestionnaire1(), "success");
    }

    @PostMapping("/2") //对应的api路径
    public Response<List<QuestionnaireDTO>> getQuestionnaire2(HttpServletRequest request, @RequestBody Answer1DTO answer)
    {
        UserDTO user= UserHolder.getUser();
        Integer userId= Integer.valueOf(user.getUserId());


//        System.out.println(answer);

//        System.out.println(questionnaireService.getQuestionnaire2(userId, answer.getResult()));

        List<QuestionnaireDTO> result = questionnaireService.getQuestionnaire2(userId, answer.getResult());
        if (result == null) {
            return Response.fail("问卷上传失败");
        }

        return Response.success(result, "success");
    }

    @PostMapping("/recommended-sport-plan") //对应的api路径
    public Response<ScenarioDTO> getRecommendedSportPlan(HttpServletRequest request, @RequestBody Answer2DTO answer)
    {
        UserDTO user= UserHolder.getUser();
        Integer userId= Integer.valueOf(user.getUserId());
        String token = request.getHeader("authorization").toString();
        System.out.println(answer);

        ScenarioDTO result = questionnaireService.getRecommendedSportPlan(userId, answer.getResult(),token);
        if (result == null) {
            return Response.fail("问卷上传失败");
        }
        return Response.success(result, "success");
    }
}
