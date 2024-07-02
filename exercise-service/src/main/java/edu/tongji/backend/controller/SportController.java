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
import edu.tongji.backend.service.IExerciseService;
import edu.tongji.backend.service.IRunningService;
//import edu.tongji.backend.util.GlobalEventChecker;
import edu.tongji.backend.util.Response;
import edu.tongji.backend.util.TimeTypeChecker;
import edu.tongji.backend.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController//用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/api/sports")//对应的api路径
public class SportController {
    @Autowired
    IExerciseService exerciseService;
    @Autowired
    IRunningService runningService;
    @GetMapping("/getExerciseIntvl")
    public Intervals getExerciseIntervals(@RequestParam("category") String category,@RequestParam("date") String date,HttpServletRequest request){
        try {
            UserDTO user = UserHolder.getUser();
            String user_id = user.getUserId();
            String token = request.getHeader("authorization");
            return exerciseService.getExerciseIntervalsInOneDay(category, user_id, date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    @GetMapping("/startDoingSport") //对应的api路径
    public Response<Null> startExercise(Double longitude, Double latitude, HttpServletRequest request)
    {
        UserDTO user= UserHolder.getUser();
        String user_id= user.getUserId();
        //确认用户是否存在，是否是病人
        try {
            Integer ans= exerciseService.addExercise(user_id,longitude,latitude,request);
            if(ans !=null&&ans!=0)
                return Response.success(null,"开始运动");
            else
                return Response.fail("数据库更新失败");
        }catch (Exception e){
            log.info(e.getMessage());
           return Response.fail(e.getMessage());
        }
    }

    @GetMapping("/stopDoingSport") //对应的api路径
    public Response<Null> stopSport(HttpServletRequest request)//把请求中的内容映射到user
    {
        UserDTO user= UserHolder.getUser();
        String user_id= user.getUserId();
        String token = request.getHeader("authorization").toString();
        //确认用户是否存在，是否是病人
        try {
            Integer ans= exerciseService.finishExercise(user_id,token);
            if(ans !=null&&ans!=0)
                return Response.success(null,"结束运动");
            else
                return Response.fail("运动方案不存在");
        }catch (Exception e){
            return Response.fail(e.getMessage());
        }
    }
    @GetMapping("/realTimeSportData")
    public Response<RealTimeSportDTO> getRealTimeSportData(Double x,Double y,HttpServletRequest request)
    {
        UserDTO user= UserHolder.getUser();
        String user_id= user.getUserId();
        //确认用户是否存在，是否是病人
        try {
            RealTimeSportDTO ans= exerciseService.getRealTimeSport(user_id,x,y);
            if(ans !=null)
                return Response.success(ans,"成功获取实时运动数据");
            else
                return Response.fail("实时运动数据不存在");
        }catch (Exception e){
            return Response.fail(e.getMessage());
        }
    }
    @GetMapping("/doctor/sportRecord")
    public Response<SportRecordDTO> getTotalRecord(String user_id,HttpServletRequest request){
        try {
            SportRecordDTO ans= exerciseService.getSportRecord(user_id);
            if(ans !=null)
                return Response.success(ans,"成功获取运动记录");
            else
                return Response.fail("运动记录不存在");
        }catch (Exception e){
            log.info(e.getMessage());
            return Response.fail(e.getMessage());
        }
    }
    @GetMapping("/sportRecord") //对应的api路径
    public Response<SportRecordDTO> getTotalRecord(HttpServletRequest request)//把请求中的内容映射到user
    {
        UserDTO user= UserHolder.getUser();
        String user_id= user.getUserId();
        //确认用户是否存在，是否是病人
        try {
            SportRecordDTO ans= exerciseService.getSportRecord(user_id);
            if(ans !=null)
                return Response.success(ans,"成功获取运动记录");
            else
                return Response.fail("运动记录不存在");
        }catch (Exception e){
            log.info(e.getMessage());
            return Response.fail(e.getMessage());
        }
    }
    @PostMapping("/detailedSportRecord")
    public Response<SportDetailedDTO> getDetailRecord
            (HttpServletRequest request, @RequestParam String time_type,@RequestParam String category)
    {
        UserDTO user= UserHolder.getUser();
        String user_id= user.getUserId();
        //确认用户是否存在，是否是病人
        try {
            int check = TimeTypeChecker.check(time_type);
            if (check == 0)
                return Response.fail("time_type is invalid");

            SportDetailedDTO ans= exerciseService.getDetailedSportRecord(user_id,check,category);
            if(ans !=null)
                return Response.success(ans,"成功获取运动记录");
            else
                return Response.fail("运动记录不存在");
        }catch (Exception e){
            log.info(e.getMessage());
            return Response.fail(e.getMessage());
        }
    }
    @GetMapping("/realTimeHeartRate")
    public Response<Integer> getRealTimeHeartRate(HttpServletRequest request)
    {
        UserDTO user= UserHolder.getUser();
        String user_id= user.getUserId();
        //确认用户是否存在，是否是病人
        try {

            Integer ans= exerciseService.getRealTimeHeartRate(user_id);
            if(ans !=null)
                return Response.success(ans,"成功获取实时心率");
            else
                return Response.fail("实时心率不存在");
        }catch (Exception e){
            return Response.fail(e.getMessage());
        }
    }
    @GetMapping("/sportPlan")
    public Response<SportPlanDTO> getSportPlan(HttpServletRequest request)
    {
        UserDTO user= UserHolder.getUser();
        String user_id= user.getUserId();
        //确认用户是否存在，是否是病人
        try {
            SportPlanDTO ans= exerciseService.getSportPlan(user_id);
            if(ans !=null)
                return Response.success(ans,"成功获取运动方案");
            else
                return Response.fail("运动方案不存在");
        }catch (Exception e){
            log.info(e.getMessage());
            return Response.fail(e.getMessage());
        }
    }

}
