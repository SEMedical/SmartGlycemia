package edu.tongji.backend.controller;

import edu.tongji.backend.dto.Sport;
import edu.tongji.backend.dto.UserDTO;
import edu.tongji.backend.util.Jwt;
import edu.tongji.backend.service.IScenarioService;
import edu.tongji.backend.util.Response;
import edu.tongji.backend.util.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController//用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/api/sports")//对应的api路径
public class ScenarioController {
    @Autowired
    IScenarioService scenarioService;

    @PostMapping("/set-scenario") //对应的api路径
    public Response<String> setScenario(HttpServletRequest request, @RequestParam String category, @RequestParam Integer minute) {
        UserDTO user= UserHolder.getUser();
        Integer userId= Integer.valueOf(user.getUserId());
//switch category
        category = category.trim();
        switch (category) {
            case "散步":
                category = "walking";
                break;
            case "瑜伽":
                category = "yoga";
                break;
            case "跳绳":
                category = "ropeskipping";
                break;
            case "慢跑":
                category = "jogging";
                break;
            default:
                return Response.fail("运动类型错误");
        }
        if (scenarioService.setScenario(userId, new Sport(category, minute))) {
            return Response.success("设置成功", "success");
        } else {
            log.error("setting the scenario failed,the category is"+category+"!");
            return Response.fail("设置失败");
        }
    }
}
