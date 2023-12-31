package edu.tongji.backend.controller;

import edu.tongji.backend.dto.*;
import edu.tongji.backend.exception.GlycemiaException;
import edu.tongji.backend.service.IExerciseService;
import edu.tongji.backend.service.IGlycemiaService;
import edu.tongji.backend.service.IProfileService;
import edu.tongji.backend.service.IUserService;
import edu.tongji.backend.util.Jwt;
import edu.tongji.backend.util.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController//用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/glycemia")//对应的api路径
public class GlycemiaController {
    @Autowired
    IGlycemiaService glycemiaService;
    @Autowired
    IUserService userService;
    @Autowired
    IProfileService profileService;
    @Autowired
    IExerciseService exerciseService;
    @GetMapping("/chart") //对应的api路径
    public Response<Chart> LookupChart(HttpServletRequest request, @RequestParam String type, @RequestParam String date)//把请求中的内容映射到user
    {
        try {
            String token = request.getHeader("Authorization");
            System.out.println(token);
            String user_id = (String) Jwt.parse(token).get("userId");
            //确认用户是否存在，是否是病人
            this.checkUser(user_id);
            //check regex pattern for date must be yyyy-mm-dd and must older than 2023-12-01
            LocalDate formattedDate = this.checkDate(date, LocalDate.of(2023, 12, 1), LocalDate.now().plusDays(1));
            //确认类型必须为history或realtime
            if (!type.equals("History") && !type.equals("Realtime"))
                throw new GlycemiaException("type must be history or realtime");
            //history必须有time
            if (type.equals("History") && date == null)
                throw new GlycemiaException("history must have time");
            Chart result = glycemiaService.showGlycemiaDiagram(type, user_id, formattedDate);
            //LOG
            System.out.println(result);
            return Response.success(result,"chart API has passed!");
        }catch (GlycemiaException e){
            System.out.println(e.getMessage());
            return Response.fail("Expected internal business exception");
        }catch (RuntimeException|Error e){
            System.err.println(e.getMessage());
            return Response.fail("Unexpected external business exception or system error!");
        }

    }
    private void checkUser(String user_id)
    {
        if(userService.getById(user_id)==null)
            throw new GlycemiaException("user doesn't exist");
        else if(!userService.getById(user_id).getRole().equals("patient"))
            throw new GlycemiaException("user isn't a patient");
        else if(profileService.getByPatientId(user_id)==null)
            throw new GlycemiaException("exception with registration of the user"+user_id);
    }
    //Check if the format of date is valid and is in the range of (start,end)
    private LocalDate checkDate(String date,LocalDate start,LocalDate end){
        if(date !=null&&!date.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$"))
            throw new GlycemiaException("date must be yyyy-mm-dd");
        LocalDate formattedDate=LocalDate.parse(date);
        if(formattedDate.isBefore(start))
            throw new GlycemiaException("date must bigger than "+start);
        if(formattedDate.isAfter(end))
            throw new GlycemiaException("date must smaller than "+end);
        return formattedDate;
    }
    @GetMapping("/chart_record") //对应的api路径
    public Response<CompositeChart> LookupChartRecord(HttpServletRequest request,@RequestParam String span,@RequestParam String startDate)//把请求中的内容映射到user
    {
        try {
            String token = request.getHeader("Authorization");
            System.out.println(token);
            String user_id = (String) Jwt.parse(token).get("userId");
            //确认用户是否存在，是否是病人
            this.checkUser(user_id);
            //check regex pattern for date must be yyyy-mm-dd and must older than 2023-12-01
            LocalDate formattedDate = this.checkDate(startDate, LocalDate.of(2023, 12, 1), LocalDate.now().minusDays(span == "Week" ? 7 : 30));
            //确认类型必须为history或realtime
            if (!span.equals("Week") && !span.equals("Month"))
                throw new GlycemiaException("span must be week or month");
            //history必须有time
            if (startDate == null)
                throw new GlycemiaException("If you want to loop up the chart record, startTime is required");
            CompositeChart result = glycemiaService.showGlycemiaHistoryDiagram(span, user_id, formattedDate);
            //LOG
            System.out.println(result);
            return Response.success(result,"Successfully get the glycemia record!");
        }catch (GlycemiaException e){
            System.out.println(e.getMessage());
            return Response.fail("Expected internal business exception");
        }catch (RuntimeException|Error e){
            System.err.println(e.getMessage());
            return Response.fail("Unexpected external business exception or system error!");
        }
    }
    @GetMapping("/is_exercise")
    public Response<Intervals> GetExerciseIntervals(HttpServletRequest request,@RequestParam String type,@RequestParam String date){
        try {
            String token = request.getHeader("Authorization");
            System.out.println(token);
            String user_id = (String) Jwt.parse(token).get("userId");
            //确认用户是否存在，是否是病人
            this.checkUser(user_id);
            //check regex pattern for date must be yyyy-mm-dd and must older than 2023-12-01
            LocalDate formattedDate = this.checkDate(date, LocalDate.of(2023, 12, 1), LocalDate.now().plusDays(1));
            //运动类型必须为慢跑或瑜伽...
            if (!type.equals("Jogging") && !type.equals("Yoga"))
                throw new GlycemiaException("exercise category must be one of the followings:" +
                        "Jogging,Yoga...");
            Intervals res = exerciseService.getExerciseIntervalsInOneDay(type, user_id, formattedDate.toString());
            return Response.success(res, "Successfully get all the intervals during a day!");
        }catch (GlycemiaException e){
            System.out.println(e.getMessage());
            return Response.fail("Expected internal business failure");
        }catch (Exception|Error e){
            System.err.println(e.getMessage());
            return Response.fail("Unexpected external business exception or system error!");
        }
    }
    @GetMapping("/realtime")
    public Response<Double> GetRealtimeGlycemia(HttpServletRequest request){
        try {
            String token = request.getHeader("Authorization");
            System.out.println(token);
            String user_id = (String) Jwt.parse(token).get("userId");
            //确认用户是否存在，是否是病人
            this.checkUser(user_id);
            Double data=glycemiaService.getLatestGlycemia(user_id);
            return Response.success(data,"You've get the latest glycemia data!");
        }catch (GlycemiaException e){
            System.out.println(e.getMessage());
            return Response.fail("Expected internal failure");
        }catch (Exception|Error e){
            System.err.println(e.getMessage());
            return Response.fail("Unexpected external failure");
        }
    }
    @GetMapping("/realTimePrompt")
    public Response<Tip> GetRealtimeTips(HttpServletRequest request){
        try {
            String token = request.getHeader("Authorization");
            System.out.println(token);
            String user_id = (String) Jwt.parse(token).get("userId");
            //确认用户是否存在，是否是病人
            this.checkUser(user_id);
            Double data=glycemiaService.getLatestGlycemia(user_id);
            Boolean AfterDinner=(LocalDateTime.now().getHour()>18&&LocalDateTime.now().getHour()<19);
            Boolean AfterLunch=(LocalDateTime.now().getHour()>12&&LocalDateTime.now().getHour()<14);
            if(data<70)//RGBA for Red
                return Response.success(new Tip("哎呀！血糖怎么有点低了呢？请吃点东西吧！", MyColor.RED),"Tips generated successfully");
            else if(data<100){
                return Response.success(new Tip("当前血糖处于正常水平，真是令人高兴呐！",MyColor.GREEN),"Tips generated successfully");
            }else if(AfterLunch&&data>140){
                return Response.success(new Tip("当前血糖水平已经高于正常值了哦，注意饮食，然后请去做一点运动吧！",MyColor.RED),"Tips generated successfully");
            }else if(AfterDinner&&data>120)
                return Response.success(new Tip("当前血糖水平已经高于正常值了哦，之后要注意不要吃太多含糖含碳水量高的食物",MyColor.RED),"Tips generated successfully");
            else if(AfterDinner ||AfterLunch){
                return Response.success(new Tip("饭后血糖上升，不必要过度担心，要时刻注重饮食哦",MyColor.YELLOW),"Tips generated successfully");
            }else{
                if(data>100)
                    return Response.success(new Tip("当前血糖水平已经高于正常值了哦，然后请去做一点运动吧！",MyColor.RED),"Tips generated successfully");
            }
        }catch (GlycemiaException e){
            System.out.println(e.getMessage());
            return Response.fail("Expected internal failure");
        }catch (Exception|Error e){
            System.err.println(e.getMessage());
            return Response.fail("Unexpected external failure");
        }
        return Response.fail("Fatal error");
    }
}
