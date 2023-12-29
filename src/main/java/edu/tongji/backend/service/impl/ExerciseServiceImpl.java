package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.dto.CategoryRecordDTO;
import edu.tongji.backend.dto.SportRecordDTO;
import edu.tongji.backend.entity.Examine;
import edu.tongji.backend.entity.Exercise;
import edu.tongji.backend.entity.Intervals;
import edu.tongji.backend.entity.Scenario;
import edu.tongji.backend.mapper.ExamineMapper;
import edu.tongji.backend.mapper.ExerciseMapper;
import edu.tongji.backend.mapper.ScenarioMapper;
import edu.tongji.backend.service.IExerciseService;
import edu.tongji.backend.util.CalorieCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ExerciseServiceImpl extends ServiceImpl<ExerciseMapper, Exercise> implements IExerciseService {
    @Autowired
    ExerciseMapper exerciseMapper;
    @Autowired
    ScenarioMapper scenarioMapper;
    @Autowired
    ExamineMapper examineMapper;

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
    @Override
    public Integer addExercise(String userId)
    {
        int user_id=Integer.parseInt(userId);
        Exercise exercise=new Exercise();
        exercise.setPatientId(user_id);
        exercise.setStartTime(LocalDateTime.now());
        //查找这个用户的运动方案
        QueryWrapper<Scenario> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("patient_id",user_id);
        List<Scenario> scenarios=scenarioMapper.selectList(queryWrapper);
        if(scenarios.size()==0)
            return null;
Scenario last_scenario=scenarios.get(scenarios.size()-1);
        exercise.setCategory(last_scenario.getCategory());
        return exerciseMapper.insert(exercise);
    }
    @Override
    public Integer finishExercise(String userId)
    {
        int user_id=Integer.parseInt(userId);
        QueryWrapper<Exercise> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("patient_id",user_id);
        List<Exercise> exercises=exerciseMapper.selectList(queryWrapper);
        if(exercises.size()==0)
            return null;
        Exercise last_exercise=exercises.get(exercises.size()-1);
        int duration=(int)Duration.between(last_exercise.getStartTime(),LocalDateTime.now()).toMinutes() ;
        last_exercise.setDuration(duration);
//获取用户体重数据
        double weight=70;
        QueryWrapper<Examine> examineQueryWrapper=new QueryWrapper<>();
        examineQueryWrapper.eq("patient_id",user_id).gt("weight",0);
        List<Examine> examines=examineMapper.selectList(examineQueryWrapper);
        if(examines.size()==0)
            ;
        else {
            Examine last_examine=examines.get(examines.size()-1);
            weight=last_examine.getWeight();
        }
//获取运动类型
        String category=last_exercise.getCategory();
        //更新卡路里
        int calorie= CalorieCalculator.getCalorie(category,weight,duration);
        last_exercise.setCalorie(calorie);
        return exerciseMapper.updateById(last_exercise);
    }

@Override
public SportRecordDTO getSportRecord(String userId)
{
    int user_id=Integer.parseInt(userId);
    QueryWrapper<Exercise> queryWrapper=new QueryWrapper<>();
    queryWrapper.eq("patient_id",user_id);
    //查找最近7天的运动记录，从7天前的0点到今天的现在
    queryWrapper.ge("start_time",LocalDate.now().minusDays(7).atStartOfDay());
    List<Exercise> exercises=exerciseMapper.selectList(queryWrapper);
    int total_minute=0;
    int total_calorie=0;
    int[] minute_record= new int[7];
    HashMap<String, CategoryRecordDTO> sport_records=new HashMap<>();
    for(Exercise exercise:exercises)
    {
        total_minute+=exercise.getDuration();
        total_calorie+=exercise.getCalorie();
        //要知道这个运动是在第几天，然后在对应的位置加上运动时间
        minute_record[LocalDate.now().minusDays(7).until(exercise.getStartTime().toLocalDate()).getDays()]+=exercise.getDuration();
//要知道这个运动是什么种类，然后在对应的位置加上运动时间
        String category=exercise.getCategory();
        if(sport_records.containsKey(category))
        {
            CategoryRecordDTO categoryRecordDTO =sport_records.get(category);
            categoryRecordDTO.setMinute(categoryRecordDTO.getMinute()+exercise.getDuration());
            categoryRecordDTO.setCalorie(categoryRecordDTO.getCalorie()+exercise.getCalorie());
            sport_records.put(category, categoryRecordDTO);
        }
        else
        {
            CategoryRecordDTO categoryRecordDTO =new CategoryRecordDTO(exercise.getDuration(),exercise.getCalorie());
            sport_records.put(category, categoryRecordDTO);
        }
    }
    return new SportRecordDTO(total_minute,total_calorie,minute_record,sport_records);

}

}
