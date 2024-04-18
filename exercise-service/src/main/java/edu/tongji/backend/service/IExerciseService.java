package edu.tongji.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.dto.RealTimeSportDTO;
import edu.tongji.backend.dto.SportDetailedDTO;
import edu.tongji.backend.dto.SportPlanDTO;
import edu.tongji.backend.dto.SportRecordDTO;
import edu.tongji.backend.entity.Exercise;
import edu.tongji.backend.dto.Intervals;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import java.util.List;

public interface IExerciseService extends IService<Exercise> {
    Intervals getExerciseIntervalsInOneDay(String category,String userId, String date);
    Integer addExercise(String userId,Double longitude,Double latitude,HttpServletRequest request);
    Integer finishExercise(String userId,String token);
    SportRecordDTO getSportRecord(String userId);
    SportDetailedDTO getDetailedSportRecord(String userId,int time_type,String category);
    Integer getRealTimeHeartRate(String userId);
    SportPlanDTO getSportPlan(String userId);
    RealTimeSportDTO getRealTimeSport(String userId,Double longitude,Double latitude) throws Exception;
}
