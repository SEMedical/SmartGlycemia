package edu.tongji.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.dto.SportRecordDTO;
import edu.tongji.backend.entity.Exercise;
import edu.tongji.backend.entity.Intervals;

import java.util.List;

public interface IExerciseService extends IService<Exercise> {
    Intervals getExerciseIntervalsInOneDay(String category,String userId, String date);
    Integer addExercise(String userId);
    Integer finishExercise(String userId);
SportRecordDTO getSportRecord(String userId);
}
