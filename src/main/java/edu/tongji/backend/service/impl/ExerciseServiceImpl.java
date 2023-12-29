package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.entity.Exercise;
import edu.tongji.backend.dto.Intervals;
import edu.tongji.backend.mapper.ExerciseMapper;
import edu.tongji.backend.service.IExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ExerciseServiceImpl extends ServiceImpl<ExerciseMapper, Exercise> implements IExerciseService {
    @Autowired
    ExerciseMapper exerciseMapper;

    @Override
    public Intervals getExerciseIntervalsInOneDay(String category,String userId, String date) {
        List<Map<String,String>> lists=exerciseMapper.getExerciseIntervalsInOneDay(category,userId, date);
        List<Map<LocalDateTime,LocalDateTime>> formattedLists=new ArrayList<>();
        Intervals intervals=new Intervals();
        for (Map<String, String> list : lists) {
            Map<String, String> datemap=new HashMap<>();
            Map.Entry<String,String>entry=list.entrySet().iterator().next();
            LocalDateTime date1=LocalDateTime.parse(entry.getKey());
            LocalDateTime date2=LocalDateTime.parse(entry.getValue());
            formattedLists.add(new HashMap<>(Map.of(date1,date2)));
            intervals.setDatas(formattedLists);
        }
        return intervals;
    }
}
