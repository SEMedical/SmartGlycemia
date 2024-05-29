package edu.tongji.backend.controller;

import edu.tongji.backend.clients.ExerciseClient;
import edu.tongji.backend.clients.UserClient;
import edu.tongji.backend.config.FeignConfig;
import edu.tongji.backend.dto.*;
import edu.tongji.backend.entity.Profile;
import edu.tongji.backend.exception.ExerciseException;
import edu.tongji.backend.exception.GlycemiaException;
import edu.tongji.backend.service.IGlycemiaService;
import edu.tongji.backend.util.Jwt;
import edu.tongji.backend.util.Response;
import edu.tongji.backend.util.TokenHolder;
import edu.tongji.backend.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Slf4j
@RestController//用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/api/glycemia")//对应的api路径
public class GlycemiaController {
    @Autowired
    IGlycemiaService glycemiaService;
    @GetMapping("/chart") //对应的api路径
    public Response<Chart> LookupChart(@RequestParam String type, @RequestParam String date)//把请求中的内容映射到user
    {
        //确认用户是否存在，是否是病人
        UserDTO user= UserHolder.getUser();
        String user_id= user.getUserId();
        return LookupChart(user_id,type,date);
    }
    public Response<Chart> LookupChart(String user_id,String type,String date){
        try {
            if(type.equals("realtime"))
                type="Realtime";
            if (!type.equals("History") && !type.equals("Realtime"))
                throw new GlycemiaException("type must be history or realtime");
            //history必须有time
            if (type.equals("History") && date == null)
                throw new GlycemiaException("history must have time");
            LocalDate formattedDate=null;
            //check regex pattern for date must be yyyy-mm-dd and must older than 2023-12-01
            if (type.equals("History"))
                formattedDate = this.checkDate(date, LocalDate.of(2023, 12, 1), LocalDate.now().plusDays(1));
            //确认类型必须为history或realtime

            Chart result = glycemiaService.showGlycemiaDiagram(type, user_id, formattedDate);
            //LOG
            log.debug(result.toString());
            return Response.success(result,"chart API has passed!");
        }catch (GlycemiaException e){
            log.debug(e.getMessage());
            return Response.fail("Expected internal business exception");
        }catch (RuntimeException|Error e){
            System.err.println(e.getMessage());
            return Response.fail("Unexpected external business exception or system error!");
        }
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

    @GetMapping("/weeklyOrMonthlyRecord") //对应的api路径
    public Response<CompositeChart> LookupChartRecord(@RequestParam String span,@RequestParam String startDate){
        //确认用户是否存在，是否是病人
        UserDTO user= UserHolder.getUser();
        String user_id= user.getUserId();
        return LookupChartRecord(user_id,span,startDate);
    }
    public Response<CompositeChart> LookupChartRecord(String user_id,String span,String startDate)//把请求中的内容映射到user
    {
        try {
            if(span.equals("week"))
                span="Week";
            if(span.equals("month"))
                span="Month";
            //check regex pattern for date must be yyyy-mm-dd and must older than 2023-12-01
            LocalDate formattedDate = this.checkDate(startDate, LocalDate.of(2023, 12, 1), LocalDate.now());
            //确认类型必须为history或realtime
            if (!span.equals("Week") && !span.equals("Month"))
                throw new GlycemiaException("span must be week or month");
            //history必须有time
            if (startDate == null)
                throw new GlycemiaException("If you want to loop up the chart record, startTime is required");
            CompositeChart result = glycemiaService.showGlycemiaHistoryDiagram(span, user_id, formattedDate);
            //LOG
            log.debug(result.toString());
            return Response.success(result,"Successfully get the glycemia record!");
        }catch (GlycemiaException e){
            log.debug(e.getMessage());
            return Response.fail("Expected internal business exception");
        }catch (RuntimeException|Error e){
            System.err.println(e.getMessage());
            return Response.fail("Unexpected external business exception or system error!");
        }
    }
    @Autowired
    ExerciseClient exerciseClient;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @GetMapping("/isExercise")
    public Response<Intervals> GetExerciseIntervals(HttpServletRequest request,@RequestParam String type,@RequestParam String date){
        try {
            UserDTO user= UserHolder.getUser();
            String user_id= user.getUserId();
            String token=request.getHeader("authorization").toString();
            //check regex pattern for date must be yyyy-mm-dd and must older than 2023-12-01
            LocalDate formattedDate = this.checkDate(date, LocalDate.of(2023, 12, 1), LocalDate.now().plusDays(1));
            //运动类型必须为慢跑或瑜伽...
            if (!type.equals("Jogging") && !type.equals("Yoga"))
                throw new ExerciseException("exercise category must be one of the followings:" +
                        "Jogging,Yoga...");
            // 2.2.发送http请求，实现远程调用
            TokenHolder.saveToken(token);
            Intervals intervals = exerciseClient.getExerciseIntervals(type, formattedDate.toString());
            TokenHolder.removeToken();
            return Response.success(intervals, "Successfully get all the intervals during a day!");
        }catch (GlycemiaException|ExerciseException e){
            log.debug(e.getMessage());
            return Response.fail("Expected internal business failure");
        }catch (Exception|Error e){
            System.err.println(e.getMessage());
            return Response.fail("Unexpected external business exception or system error!");
        }
    }
    @GetMapping("/realTime")
    public Response<Double> GetRealtimeGlycemia(HttpServletRequest request){
        try {
            UserDTO user= UserHolder.getUser();
            String user_id= user.getUserId();
            Double data=glycemiaService.getLatestGlycemia(user_id);
            return Response.success(data,"You've get the latest glycemia data!");
        }catch (GlycemiaException e){
            log.debug(e.getMessage());
            return Response.fail("Expected internal failure");
        }catch (Exception|Error e){
            System.err.println(e.getMessage());
            return Response.fail("Unexpected external failure");
        }
    }
    @GetMapping("dailyHistory")
    public Response<DailyChart> GetDailyChart(HttpServletRequest request,@RequestParam String date){
        try {
            UserDTO user= UserHolder.getUser();
            String user_id= user.getUserId();
            //check regex pattern for date must be yyyy-mm-dd and must older than 2023-12-01
            LocalDate formattedDate = this.checkDate(date, LocalDate.of(2023, 12, 1), LocalDate.now().plusDays(1));

            DailyChart result = glycemiaService.showDailyGlycemiaDiagram(user_id, formattedDate);
            log.debug(result.toString());
            return Response.success(result,"dailychart API has passed!");
        }catch (GlycemiaException e){
            log.debug(e.getMessage());
            return Response.fail("Expected internal business exception");
        }catch (RuntimeException|Error e){
            System.err.println(e.getMessage());
            return Response.fail("Unexpected external business exception or system error!");
        }
    }
    @Autowired
    UserClient userClient;
    public Response<Tip> GetRealtimeTips(String user_id,Integer age){
        try {
            Double data = glycemiaService.getLatestGlycemia(user_id);
            GlycemiaLevel level = glycemiaService.GetGlycemiaLevel(Double.valueOf(age), LocalDateTime.now(), data);
            switch (level) {
                case HYPOGLYCEMIA:
                    return Response.success(new Tip("哎呀！血糖怎么有点低了呢？请吃点东西吧！", MyColor.ORANGE), "Tips generated successfully");
                case EUGLYCEMIA:
                    return Response.success(new Tip("当前血糖处于正常水平，真是令人高兴呐！", MyColor.GREEN), "Tips generated successfully");
                case AFTER_LUNCH_HYPER:
                    return Response.success(new Tip("当前血糖水平已经高于正常值了哦，注意饮食，然后请去做一点运动吧！", MyColor.RED), "Tips generated successfully");
                case AFTER_DINNER_HYPER:
                    return Response.success(new Tip("当前血糖水平已经高于正常值了哦，之后要注意不要吃太多含糖含碳水量高的食物", MyColor.RED), "Tips generated successfully");
                case AFTER_MEAL_A_BIT_HYPER:
                    return Response.success(new Tip("饭后血糖上升，不必要过度担心，要时刻注重饮食哦", MyColor.YELLOW), "Tips generated successfully");
                case HYPERGLYCEMIA:
                    return Response.success(new Tip("当前血糖水平已经高于正常值了哦，然后请去做一点运动吧！", MyColor.RED), "Tips generated successfully");
                default:
                    return Response.fail("Fatal error");
            }
        }catch (GlycemiaException e){
                log.debug(e.getMessage());
                return Response.fail("Expected internal failure");
        }catch (Exception|Error e){
                System.err.println(e.getMessage());
                return Response.fail("Unexpected external failure");
        }
    }
    @GetMapping("/realTimePrompt")
    public Response<Tip> GetRealtimeTips(HttpServletRequest request){
        //glycemiaService.Init_LatestGlycemiaDiagram();
        UserDTO user= UserHolder.getUser();
        String user_id= user.getUserId();
        String token = request.getHeader("authorization");
        TokenHolder.saveToken(token);
        Response<Integer> age0 =userClient.getUserAge();
        TokenHolder.removeToken();
        Integer age=age0.getResponse();
        return GetRealtimeTips(user_id,age);
    }
}
