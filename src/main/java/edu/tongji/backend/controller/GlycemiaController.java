package edu.tongji.backend.controller;

import edu.tongji.backend.entity.Chart;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.service.IGlycemiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController//用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/glycemia")//对应的api路径
public class GlycemiaController {
    @Autowired
    IGlycemiaService glycemiaService;
    @GetMapping("/chart") //对应的api路径
    public Chart LookupChart(@RequestParam String user_id, @RequestParam String type)//把请求中的内容映射到user
    {
        //TODO 确认用户是否存在，是否是病人

        //TODO 确认类型必须为history或realtime
        if(!type.equals("History")&&!type.equals("Realtime"))
            throw new RuntimeException("type must be history or realtime");
        //history必须有time
        Chart result=glycemiaService.showGlycemiaDiagram(type,user_id,null);
        //LOG
        System.out.println(result);
        return result;
    }
}
