package edu.tongji.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.entity.Exercise;
import edu.tongji.backend.dto.Intervals;

public interface IExerciseService extends IService<Exercise> {
    Intervals getExerciseIntervalsInOneDay(String category,String userId, String date);
}
