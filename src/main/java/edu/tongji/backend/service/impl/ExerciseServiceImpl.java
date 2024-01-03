package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.dto.ExerciseDTO;
import edu.tongji.backend.entity.Exercise;
import edu.tongji.backend.dto.Intervals;
import edu.tongji.backend.exception.ExerciseException;
import edu.tongji.backend.mapper.ExerciseMapper;
import edu.tongji.backend.service.IExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ExerciseServiceImpl extends ServiceImpl<ExerciseMapper, Exercise> implements IExerciseService {
    @Autowired
    ExerciseMapper exerciseMapper;

    @Override
    public Intervals getExerciseIntervalsInOneDay(String category,String userId, String date) {
        List<ExerciseDTO> lists=exerciseMapper.getExerciseIntervalsInOneDay(category,userId, date);
        List<Map<LocalDateTime,LocalDateTime>> formattedLists=new ArrayList<>();
        Intervals intervals=new Intervals();
        for (ExerciseDTO list : lists) {
            if(list.getStartTime()==null){
                throw new ExerciseException("startTime is null ");
            }else if(list.getDuration()==null){
                throw new ExerciseException("duration is null ");
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime date1 = LocalDateTime.parse(list.getStartTime(), formatter);
            LocalDateTime date2=date1.plusMinutes(list.getDuration());
            System.out.println(date2);
            formattedLists.add(new HashMap<>(Map.of(date1,date2)));
            intervals.setDatas(formattedLists);
        }
        return intervals;
    }
}
