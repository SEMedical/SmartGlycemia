package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.dto.CategoryRecordDTO;
import edu.tongji.backend.dto.SportDetailedDTO;
import edu.tongji.backend.dto.SportPlanDTO;
import edu.tongji.backend.dto.SportRecordDTO;
import edu.tongji.backend.entity.*;
import edu.tongji.backend.mapper.ExamineMapper;
import edu.tongji.backend.mapper.ExerciseMapper;
import edu.tongji.backend.mapper.RunningMapper;
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
    @Autowired
    RunningMapper runningMapper;

    @Override
    public Intervals getExerciseIntervalsInOneDay(String category, String userId, String date) {
        List<Map<String, String>> lists = exerciseMapper.getExerciseIntervalsInOneDay(category, userId, date);
        List<Map<LocalDateTime, LocalDateTime>> formattedLists = new ArrayList<>();
        Intervals intervals = new Intervals();
        for (Map<String, String> list : lists) {
            Map<String, String> datemap = new HashMap<>();
            Map.Entry<String, String> entry = list.entrySet().iterator().next();
            LocalDateTime date1 = LocalDateTime.parse(entry.getKey());
            LocalDateTime date2 = LocalDateTime.parse(entry.getValue());
            formattedLists.add(new HashMap<>(Map.of(date1, date2)));
            intervals.setDatas(formattedLists);
        }
        return intervals;
    }

    @Override
    public Integer addExercise(String userId) {
        int user_id = Integer.parseInt(userId);
        Exercise exercise = new Exercise();
        exercise.setPatientId(user_id);
        exercise.setStartTime(LocalDateTime.now());
        //查找这个用户的运动方案
        QueryWrapper<Scenario> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("patient_id", user_id);
        List<Scenario> scenarios = scenarioMapper.selectList(queryWrapper);
        if (scenarios.isEmpty())
            return null;
        Scenario last_scenario = scenarios.get(scenarios.size() - 1);
        exercise.setCategory(last_scenario.getCategory());
        System.out.println("找到的运动种类为"+exercise.getCategory());
        int insert_exercise = exerciseMapper.insert(exercise);//往exercise表里插入一条记录
        if (insert_exercise == 0)
            return null;
        int insert_running=1;
        //如果是跑步，还要往running表里插入一条记录
        if (exercise.getCategory().equals("running")) {
            Running running = new Running();
            running.setExerciseId(exercise.getExerciseId());
            //running表里有：distance,pace
            //他们是随着运动过程中不断变化的
            insert_running=runningMapper.insert(running);
        }
        return insert_exercise*insert_running;
    }

    @Override
    public Integer finishExercise(String userId) {
        int user_id = Integer.parseInt(userId);
        QueryWrapper<Exercise> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("patient_id", user_id);
        List<Exercise> exercises = exerciseMapper.selectList(queryWrapper);
        if (exercises.isEmpty())
            return null;
        Exercise last_exercise = exercises.get(exercises.size() - 1);

        int duration = (int) Duration.between(last_exercise.getStartTime(), LocalDateTime.now()).toMinutes();
        last_exercise.setDuration(duration);
//获取用户体重数据
        double weight = 70;
        QueryWrapper<Examine> examineQueryWrapper = new QueryWrapper<>();
        examineQueryWrapper.eq("patient_id", user_id).gt("weight", 0);
        List<Examine> examines = examineMapper.selectList(examineQueryWrapper);
        if (examines.isEmpty())
            ;
        else {
            Examine last_examine = examines.get(examines.size() - 1);
            weight = last_examine.getWeight();
        }
//获取运动类型
        String category = last_exercise.getCategory();
        //更新卡路里
        int calorie = CalorieCalculator.getCalorie(category, weight, duration);
        last_exercise.setCalorie(calorie);
        return exerciseMapper.updateById(last_exercise);
    }

    @Override
    public SportRecordDTO getSportRecord(String userId) {
        int user_id = Integer.parseInt(userId);
        QueryWrapper<Exercise> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("patient_id", user_id);
        //查找最近7天的运动记录，从7天前的0点到今天的现在
        queryWrapper.ge("start_time", LocalDate.now().minusDays(7).atStartOfDay());
        List<Exercise> exercises = exerciseMapper.selectList(queryWrapper);
        int total_minute = 0;
        int total_calorie = 0;
        int[] minute_record = new int[7];
        HashMap<String, CategoryRecordDTO> sport_records = new HashMap<>();
        for (Exercise exercise : exercises) {
            total_minute += exercise.getDuration();
            total_calorie += exercise.getCalorie();
            //要知道这个运动是在第几天，然后在对应的位置加上运动时间
            minute_record[LocalDate.now().minusDays(7).until(exercise.getStartTime().toLocalDate()).getDays()] += exercise.getDuration();
//要知道这个运动是什么种类，然后在对应的位置加上运动时间
            String category = exercise.getCategory();
            if (sport_records.containsKey(category)) {
                CategoryRecordDTO categoryRecordDTO = sport_records.get(category);
                categoryRecordDTO.setMinute(categoryRecordDTO.getMinute() + exercise.getDuration());
                categoryRecordDTO.setCalorie(categoryRecordDTO.getCalorie() + exercise.getCalorie());
                sport_records.put(category, categoryRecordDTO);
            } else {
                CategoryRecordDTO categoryRecordDTO = new CategoryRecordDTO(exercise.getDuration(), exercise.getCalorie());
                sport_records.put(category, categoryRecordDTO);
            }
        }
        return new SportRecordDTO(total_minute, total_calorie, minute_record, sport_records);

    }

    @Override
    public SportDetailedDTO getDetailedSportRecord(String userId, int time_type, String category) {
        int user_id = Integer.parseInt(userId);
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = LocalDate.now().atStartOfDay();
        int[] minute_record, calorie_record;
        //根据时间类型来确定查询的时间范围 1是一个月 2是一周 3是一天
        //如果是一天，就不用有minute_record和calorie_record
        switch (time_type) {
            case 1:
                startTime = LocalDate.now().minusDays(30).atStartOfDay();
                minute_record = new int[30];
                calorie_record = new int[30];
                break;
            case 2:
                startTime = LocalDate.now().minusDays(7).atStartOfDay();
                minute_record = new int[7];
                calorie_record = new int[7];
                break;
            default:
                minute_record = new int[1];
                calorie_record = new int[1];
                break;
        }
        int i = 0;
        int sum_duration = 0;//总时长，从exercise表获得
        double sum_distance = 0;//总距离，从running表获得,以公里为单位
        int mean_pace = 0;//平均配速，从running表获得 单位是秒
        int running_times=0;//跑步次数，从running表获得
        //令startTime不断加一天，直到等于endTime
        while (startTime.isBefore(endTime)) {
            startTime = startTime.plusDays(1);
            i++;
            QueryWrapper<Exercise> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("patient_id", user_id).eq("category", category.toLowerCase());
            //查找这一天的运动记录
            queryWrapper.ge("start_time", startTime).lt("start_time", startTime.plusDays(1));
            List<Exercise> exercises = exerciseMapper.selectList(queryWrapper);//获得这一条的所有运动记录
            for (Exercise exercise : exercises) {
                if(minute_record!=null&& i<minute_record.length)//防止i越界
                { minute_record[i] += exercise.getDuration();
                calorie_record[i] += exercise.getCalorie();}
                sum_duration += exercise.getDuration();
                int exercise_id = exercise.getExerciseId();
                Running running=runningMapper.selectById(exercise_id);
                if(running!=null)
                {
                    sum_distance+=running.getDistance();
                    mean_pace+=running.getPace();
                    running_times++;
                }
            }
        }
        if(running_times!=0)
            mean_pace/=running_times;
        else
            mean_pace=0;

        //把minute_space格式化为xx分xx秒这样的字符串
        String mean_speed = String.format("%d分%d秒",mean_pace/60,mean_pace%60);
        //类似地，处理sum_duration，它的单位是分钟
        String sum_duration_str;
        if(sum_duration>60)
            sum_duration_str = String.format("%d小时%d分",sum_duration/60,sum_duration%60);
        else
            sum_duration_str = String.format("%d分",sum_duration);
        return new SportDetailedDTO(minute_record, calorie_record, mean_speed, sum_duration_str, sum_distance);
    }
    @Override
    public Integer getRealTimeHeartRate(String userId) {
        //获取一个在70到100之间的随机数
        Random random = new Random();
        return random.nextInt(30)+70;
    }
    @Override
    public SportPlanDTO getSportPlan(String userId){
        int user_id = Integer.parseInt(userId);
        QueryWrapper<Scenario> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("patient_id", user_id);
        List<Scenario> scenarios = scenarioMapper.selectList(queryWrapper);
        if(scenarios.isEmpty())
            return null;
        Scenario last_scenario = scenarios.get(scenarios.size()-1);
        String category = last_scenario.getCategory();
        int recommend_time = last_scenario.getDuration();
        int recommend_calorie = last_scenario.getCalories();
        //接下来要计算 当天这个用户这个运动类型的运动总时长和总卡路里
        QueryWrapper<Exercise> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("patient_id", user_id).eq("category", category.toLowerCase());
        queryWrapper1.ge("start_time", LocalDate.now().atStartOfDay()).isNotNull("end_time");
        List<Exercise> exercises = exerciseMapper.selectList(queryWrapper1);
        int total_time = 0;
        int total_calorie = 0;
        for(Exercise exercise:exercises)
        {
            total_time+=exercise.getDuration();
            total_calorie+=exercise.getCalorie();
        }
        Boolean is_finished = false;
        if(total_time>=recommend_time)
            is_finished=true;
        return new SportPlanDTO(total_time,total_calorie,category,recommend_time,recommend_calorie,is_finished);
    }

}
