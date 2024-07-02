package edu.tongji.backend.controller;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 a-little-dust
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */





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
