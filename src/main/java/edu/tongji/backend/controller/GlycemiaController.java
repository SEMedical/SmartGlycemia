package edu.tongji.backend.controller;

import edu.tongji.backend.entity.Chart;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.service.IGlycemiaService;
import edu.tongji.backend.service.IProfileService;
import edu.tongji.backend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;

@RestController//用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/glycemia")//对应的api路径
public class GlycemiaController {
    @Autowired
    IGlycemiaService glycemiaService;
    @Autowired
    IUserService userService;
    @Autowired
    IProfileService profileService;
    @GetMapping("/chart") //对应的api路径
    public Chart LookupChart(@RequestParam String type,@RequestParam String user_id,@RequestParam String date)//把请求中的内容映射到user
    {
        //确认用户是否存在，是否是病人
        if(userService.getById(user_id)==null)
            throw new RuntimeException("user doesn't exist");
        else if(!userService.getById(user_id).getRole().equals("patient"))
            throw new RuntimeException("user isn't a patient");
        else if(profileService.getByPatientId(user_id)==null)
            throw new RuntimeException("exception with registration of the user"+user_id);
        //check regex pattern for date must be yyyy-mm-dd and must older than 2023-12-01
        if(date!=null&&!date.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$"))
            throw new RuntimeException("date must be yyyy-mm-dd");
        LocalDate formattedDate=LocalDate.parse(date);
        if(formattedDate.isBefore(LocalDate.of(2023,12,1)))
            throw new RuntimeException("date must older than 2023-12-01");
        if(formattedDate.isAfter(LocalDate.now().plus(Duration.ofDays(1))))
            throw new RuntimeException("date must older than tomorrow");
        //确认类型必须为history或realtime
        if(!type.equals("History")&&!type.equals("Realtime"))
            throw new RuntimeException("type must be history or realtime");
        //history必须有time
        if(type.equals("History")&&date==null)
            throw new RuntimeException("history must have time");
        Chart result=glycemiaService.showGlycemiaDiagram(type,user_id,formattedDate);
        //LOG
        System.out.println(result);
        return result;
    }
}
