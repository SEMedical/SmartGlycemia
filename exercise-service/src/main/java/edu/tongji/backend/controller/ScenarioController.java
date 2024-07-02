package edu.tongji.backend.controller;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 Victor Hu
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




import edu.tongji.backend.dto.Sport;
import edu.tongji.backend.dto.UserDTO;
import edu.tongji.backend.util.Jwt;
import edu.tongji.backend.service.IScenarioService;
import edu.tongji.backend.util.Response;
import edu.tongji.backend.util.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController//用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/api/sports")//对应的api路径
public class ScenarioController {
    @Autowired
    IScenarioService scenarioService;

    @PostMapping("/set-scenario") //对应的api路径
    public Response<String> setScenario(HttpServletRequest request, @RequestParam String category, @RequestParam Integer minute) {
        UserDTO user= UserHolder.getUser();
        Integer userId= Integer.valueOf(user.getUserId());
        String token = request.getHeader("authorization").toString();
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
        if (scenarioService.setScenario(userId, new Sport(category, minute),token)) {
            return Response.success("设置成功", "success");
        } else {
            return Response.fail("设置失败");
        }
    }
}
