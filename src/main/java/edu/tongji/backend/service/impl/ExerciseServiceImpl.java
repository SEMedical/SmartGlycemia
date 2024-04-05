package edu.tongji.backend.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.dto.*;
import edu.tongji.backend.entity.*;
import edu.tongji.backend.exception.ExerciseException;
import edu.tongji.backend.mapper.ExamineMapper;
import edu.tongji.backend.mapper.ExerciseMapper;
import edu.tongji.backend.mapper.RunningMapper;
import edu.tongji.backend.mapper.ScenarioMapper;
import edu.tongji.backend.service.IExerciseService;
import edu.tongji.backend.service.IProfileService;
import edu.tongji.backend.util.CalorieCalculator;
import edu.tongji.backend.util.SimpleRedisLock;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static edu.tongji.backend.util.BloomFilterUtil.exercise_running_bf;
import static edu.tongji.backend.util.BloomFilterUtil.glycemia_bf;
import static edu.tongji.backend.util.RedisConstants.*;
import static java.lang.Thread.sleep;

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
    @Autowired
    RunningServiceImpl runningService;
    @Autowired
    IProfileService profileService;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    RedisTemplate redisTemplate;
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

    @Override
    @Transactional
    public Integer addExercise(String userId,Double longitude,Double latitude) {
        SimpleRedisLock lock = new SimpleRedisLock("user:"+userId, stringRedisTemplate);
        boolean isLock = lock.tryLock(3600L);
        if(isLock) {
            Integer insert_exercise = 0;
            int insert_running =  0;
            try{
                int user_id = Integer.parseInt(userId);
                Exercise exercise = new Exercise();
                exercise.setPatientId(user_id);
                exercise.setStartTime(LocalDateTime.now());
                System.out.println("开始时间为" + exercise.getStartTime());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                //查找这个用户的运动方案
                QueryWrapper<Scenario> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("patient_id", user_id);
                List<Scenario> scenarios = scenarioMapper.selectList(queryWrapper);
                if (scenarios.isEmpty())
                    return null;
                Scenario last_scenario = scenarios.get(scenarios.size() - 1);
                exercise.setCategory(last_scenario.getCategory());
                System.out.println("找到的运动种类为" + exercise.getCategory());

                Integer weight=70;

                Profile profile = profileService.getByPatientId(userId);
                if (profile != null && profile.getWeight() != null&&profile.getWeight()>0) {
                    weight = profile.getWeight();
                    stringRedisTemplate.opsForHash().put(USER_PROFILE_KEY+userId,"weight",weight.toString());
                    stringRedisTemplate.expire(USER_PROFILE_KEY+userId,USER_PROFILE_TTL,
                            TimeUnit.MINUTES);
                }
                QueryWrapper<Exercise> exerciseQueryWrapper = new QueryWrapper<>();
                exerciseQueryWrapper.eq("patient_id", user_id);
                //TODO

                Object lastid = stringRedisTemplate.opsForValue().get(CACHE_USER_LAST_EXERCISE_KEY + user_id);
                Integer exercise_id=0;
                if(lastid!=null&&StrUtil.isNotBlank(lastid.toString()))
                    exercise_id=Integer.valueOf(lastid.toString())+1;
                else
                    exercise_id = exerciseMapper.selectList(exerciseQueryWrapper).
                            get(exerciseMapper.selectList(exerciseQueryWrapper).size() - 1).getExerciseId();
                stringRedisTemplate.opsForValue().set(CACHE_USER_LAST_EXERCISE_KEY + user_id,String.valueOf(exercise_id));
                exercise.setExerciseId(exercise_id);
                insert_exercise = exerciseMapper.insert(exercise);//往exercise表里插入一条记录
                if (insert_exercise == 0) {
                    System.out.println("插入exercise表失败");
                    return null;
                }
                insert_exercise = exercise_id;
                stringRedisTemplate.opsForValue().set(CACHE_USER_LAST_EXERCISE_KEY + user_id,
                        exercise_id.toString());
                Map<String,String> maps=new HashMap<>();
                maps.put("startTime",LocalDateTime.now().format(formatter));
                maps.put("duration","0");
                maps.put("category",exercise.getCategory());
                stringRedisTemplate.opsForHash().putAll(CACHE_EXERCISE_KEY+exercise_id, maps);
                stringRedisTemplate.expire(CACHE_EXERCISE_KEY+exercise_id,
                        CACHE_RUNNING_TTL, TimeUnit.MINUTES);
                stringRedisTemplate.expire(CACHE_RUNNING_KEY+exercise_id,
                        CACHE_RUNNING_TTL, TimeUnit.MINUTES);
                System.out.println("插入exercise表成功，exercise_id为" + insert_exercise);

                insert_running = 1;
                //如果是跑步，还要往running表里插入一条记录
                if (exercise.getCategory().equalsIgnoreCase("walking") || exercise.getCategory().equalsIgnoreCase("jogging")) {
                    Running running = new Running();
                    running.setExerciseId(exercise_id);
                    stringRedisTemplate.opsForGeo().add(RUNNING_GEO_KEY+exercise_id,new Point(longitude,latitude),
                            exercise_id.toString());
                    stringRedisTemplate.expire(RUNNING_GEO_KEY+exercise_id,
                            RUNNING_GEO_TTL,TimeUnit.MINUTES);
                    Map<String,String> map2=new HashMap<>();
                    map2.put("distance","0.0");
                    map2.put("pace","0");
                    stringRedisTemplate.opsForHash().putAll(CACHE_RUNNING_KEY+exercise_id,map2);
                    //running表里有：distance,pace
                    //他们是随着运动过程中不断变化的
                    //insert_running = runningMapper.insert(running);
                    System.out.println("插入running表成功");
                    exercise_running_bf.put(EXERCISE_RUNNING_KEY + insert_exercise);
                }else
                    System.out.println("Opps!");
                return insert_exercise * insert_running;
            }catch(Exception e) {
                lock.unlock();
                return -1;
            }
        }else
            return -1;
    }
    public void Init_exerciseRunning(){
        QueryWrapper<Running> queryWrapper = new QueryWrapper<>();
        List<Running> glycemias = runningMapper.selectList(queryWrapper);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        glycemias.forEach(element->glycemia_bf.put(
                CACHE_GLYCEMIA_KEY+element.getExerciseId()));
    }
    //TODO
    @Override
    public Integer finishExercise(String userId) {//它应该要结束当前用户的所有运动记录
        SimpleRedisLock lock = new SimpleRedisLock("user:"+userId, stringRedisTemplate);
        lock.unlock();
        int exercise_id=Integer.parseInt(stringRedisTemplate.opsForValue().get(CACHE_USER_LAST_EXERCISE_KEY+userId).toString());
        QueryWrapper<Exercise> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("exercise_id",exercise_id);
        List<Exercise> exercises = exerciseMapper.selectList(queryWrapper);
        if (exercises.isEmpty())
            return null;
        //获取用户体重数据
        int weight=70;
        Profile profile = profileService.getByPatientId(userId);
        if (profile != null && profile.getWeight() != null&&profile.getWeight()>0) {
            weight = profile.getWeight();
            System.out.println("weight: " + weight);
        }
        //转换时区
        ZoneId currentZoneId = ZoneId.systemDefault();
        Integer res=1;
        Exercise last_exercise=exercises.get(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //遍历每一个exercise
        LocalDateTime start_time = LocalDateTime.parse(stringRedisTemplate.opsForHash().get(CACHE_EXERCISE_KEY+exercise_id,"startTime").toString()
        ,formatter);
        //LocalDateTime start_time = start_time0.withZoneSameInstant(currentZoneId).toLocalDateTime();
        int duration = (int) Duration.between(start_time, LocalDateTime.now()).toMinutes();
        last_exercise.setDuration(duration);
        //获取运动类型
        String category = last_exercise.getCategory();
        //更新卡路里
        Integer calorie = CalorieCalculator.getCalorie(category.toLowerCase(), weight, duration);
        last_exercise.setCalorie(calorie);
        stringRedisTemplate.opsForHash().put(CACHE_EXERCISE_KEY+exercise_id,"calorie",calorie.toString());
        //更新distance
        if(category.equalsIgnoreCase("walking")||category.equalsIgnoreCase("jogging"))
        {
            Integer exerciseId = last_exercise.getExerciseId();
            //Bloom Filter Hit
            if(exercise_running_bf.mightContain(EXERCISE_RUNNING_KEY+exerciseId)) {
                Object pace = stringRedisTemplate.opsForHash().get(CACHE_RUNNING_KEY + exerciseId, "pace");
                Object distance =stringRedisTemplate.opsForHash().get(CACHE_RUNNING_KEY + exerciseId, "distance");
                Running running =new Running();
                if (pace != null && distance != null) {
                    running.setPace(Integer.parseInt(pace.toString()));
                    running.setDistance(Double.parseDouble(distance.toString())/1000);
                    running.setExerciseId(exerciseId);
                    System.out.println("test" + running);
                    runningMapper.insert(running);
                }
            }
        }
            //创建这个exercise对应的mapper
        res*= exerciseMapper.updateById(last_exercise);
        if (res > 0)
            System.out.println("结束exercise成功，exercise_id为" + last_exercise.getExerciseId());
        return res;
    }

    @Override
    public SportRecordDTO getSportRecord(String userId) {
        LocalDateTime startTime_sh = LocalDate.now().minusDays(6).atStartOfDay();//要改成UTC时区
        ZoneId currentZoneId = ZoneId.systemDefault();
        ZonedDateTime start_time0 = startTime_sh.atZone(currentZoneId);
        LocalDateTime startTime = start_time0.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();

        SportRecordDTO ans= new SportRecordDTO();
        //System.out.println("数组长度为"+ans.getMinute_record().length);
        int user_id = Integer.parseInt(userId);
        QueryWrapper<Exercise> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("patient_id", user_id).isNotNull("duration");
        //查找最近7天的运动记录，从7天前的0点到今天的现在
        queryWrapper.ge("start_time", startTime);
        System.out.println("起始日期为" + startTime_sh+"UTC时区的起始日期为"+startTime);

        List<Exercise> exercises = exerciseMapper.selectList(queryWrapper);
        int total_minute = 0;
        int total_calorie = 0;
        HashMap<String, CategoryRecordDTO> sport_records = new HashMap<>();
        for (Exercise exercise : exercises) {
            //再次检查一遍start_time是否晚于startTime_sh
            if (exercise.getStartTime().isBefore(startTime_sh))
                continue;
            total_minute += exercise.getDuration();
            total_calorie += exercise.getCalorie();
            //获取这个运动的开始时间，转为default时区
            LocalDateTime start_time1 = exercise.getStartTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(currentZoneId).toLocalDateTime();
            //计算start_time1和startTime_sh的差值，得到这个运动是在第几天
            int day = (int) startTime_sh.until(start_time1.withHour(0).withMinute(0).withSecond(0), ChronoUnit.DAYS);
            //System.out.println("day为" + day + "start_time1为" + start_time1 + "startTime_sh为" + startTime_sh);
            //要知道这个运动是在第几天，然后在对应的位置加上运动时间

            ans.getMinute_record()[day] += exercise.getDuration();
            //System.out.println("在第" + day + "天运动了" + exercise.getDuration() + "分钟");
            //System.out.println("这一天的日期是" + exercise.getStartTime().toLocalDate());
            //要知道这个运动是什么种类，然后在对应的位置加上运动时间
            String category = Scenario.check(exercise.getCategory());
            if (category == null)
                continue;
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
        ans.setTotal_minute(total_minute);
        ans.setTotal_calorie(total_calorie);
        ans.setSport_records(sport_records);
        return ans;
    }

    @Override
    public SportDetailedDTO getDetailedSportRecord(String userId, int time_type, String category) {
        SportDetailedDTO ans = new SportDetailedDTO();
        int user_id = Integer.parseInt(userId);
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = LocalDate.now().atStartOfDay();
        //根据时间类型来确定查询的时间范围 1是一个月 2是一周 3是一天
        //如果是一天，就不用有minute_record和calorie_record
        switch (time_type) {
            case 1:
                startTime = LocalDate.now().minusDays(29).atStartOfDay();
                ans.setMinute_record(new int[30]);
                ans.setCalorie_record(new int[30]);
                break;
            case 2:
                startTime = LocalDate.now().minusDays(6).atStartOfDay();
                ans.setMinute_record(new int[7]);
                ans.setCalorie_record(new int[7]);
                break;
            default:
                ans.setMinute_record(new int[1]);
                ans.setCalorie_record(new int[1]);
                break;
        }
        System.out.println("起始日期为"+startTime+"终止日期为"+endTime+"运动种类为"+category+"时间类型为"+time_type);
        //需要把start_time和end_time转换为UTC时区
        ZoneId currentZoneId = ZoneId.systemDefault();
        ZonedDateTime start_time0 = startTime.atZone(currentZoneId);
        ZonedDateTime end_time0 = endTime.atZone(currentZoneId);
        startTime = start_time0.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        endTime = end_time0.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        int i = 0;
        int sum_duration = 0;//总时长，从exercise表获得
        double sum_distance = 0;//总距离，从running表获得,以公里为单位
        int mean_pace = 0;//平均配速，从running表获得 单位是秒
        int running_times=0;//跑步次数，从running表获得
        //令startTime不断加一天，直到等于endTime
        while (startTime.isBefore(endTime)) {
            QueryWrapper<Exercise> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("patient_id", user_id).eq("category", category.toLowerCase());
            //查找这一天指定种类的运动记录
            queryWrapper.ge("start_time", startTime).lt("start_time", startTime.plusDays(1));
            queryWrapper.isNotNull("duration");
            List<Exercise> exercises = exerciseMapper.selectList(queryWrapper);//获得这一条的所有运动记录
            for (Exercise exercise : exercises) {
                ans.getCalorie_record()[i] += exercise.getCalorie();
                ans.getMinute_record()[i] += exercise.getDuration();
                sum_duration += exercise.getDuration();
                int exercise_id = exercise.getExerciseId();
                System.out.println("exercise_id为"+exercise_id);
                if (category.equalsIgnoreCase("walking") || category.equalsIgnoreCase("jogging"))
                {
                    //根据exercise_id查找running表
                    Running running=runningMapper.getByExerciseIdRunning(exercise_id);
                    if (running != null) {
                        System.out.println("获得的distance为"+running.getDistance());
                        sum_distance += running.getDistance();
                        mean_pace += running.getPace();
                        running_times++;
                    }
                }
            }
            startTime = startTime.plusDays(1);
            i++;
        }
        if(running_times!=0)
            mean_pace/=running_times;
        else
            mean_pace=0;

        //把minute_space格式化为xx分xx秒这样的字符串
        //如果不是跑步，就返回空字符串
        String mean_speed="";
        if (category.equalsIgnoreCase("walking") || category.equalsIgnoreCase("jogging"))
            mean_speed = String.format("%d分%d秒",mean_pace/60,mean_pace%60);
        //类似地，处理sum_duration，它的单位是分钟
        String sum_duration_str;
        if(sum_duration>60)
            sum_duration_str = String.format("%d小时%d分",sum_duration/60,sum_duration%60);
        else
            sum_duration_str = String.format("%d分",sum_duration);
        ans.setMean_speed(mean_speed);
        ans.setSum_duration(sum_duration_str);
        ans.setSum_distance(sum_distance);
        return ans;
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
        //根据patient_id可以找到唯一的运动方案
        Scenario last_scenario =scenarioMapper.selectOne(queryWrapper);
        String category = last_scenario.getCategory();
        int recommend_time = last_scenario.getDuration();
        int recommend_calorie = last_scenario.getCalories();
        //需要定义start_time，它表示今天的零点。并且因为数据库里的数据是UTC时区的，所以要转换时区
        ZoneId currentZoneId = ZoneId.systemDefault();
        ZonedDateTime start_time0=LocalDate.now().atStartOfDay().atZone(currentZoneId);
        LocalDateTime start_time = start_time0.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
       // System.out.println("开始时间为"+start_time);
        //接下来要计算 当天这个用户这个运动类型的运动总时长和总卡路里
        QueryWrapper<Exercise> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("patient_id", user_id).eq("category", category.toLowerCase());
        queryWrapper1.ge("start_time", start_time).isNotNull("duration");
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
    @Override
    public RealTimeSportDTO getRealTimeSport(String userId,Double longitude,Double latitude) throws Exception {
        int user_id = Integer.parseInt(userId);
        //获取用户体重数据
        int weight=70;

        weight=Integer.parseInt(stringRedisTemplate.opsForHash().get(USER_PROFILE_KEY+userId,"weight").toString());
        RealTimeSportDTO ans = new RealTimeSportDTO();
        LocalDateTime now = LocalDateTime.now();
        //获取最近一次运动记录
        String exerciseId = stringRedisTemplate.opsForValue().get(CACHE_USER_LAST_EXERCISE_KEY + user_id);
        QueryWrapper<Exercise> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("exercise_id", exerciseId);
        List<Exercise> exercises = exerciseMapper.selectList(queryWrapper);
        Exercise last_exercise = exercises.get(0);
        System.out.println("最近一次运动的id为"+last_exercise.getExerciseId());
        //统一时区，把start_time转为当前时区
        ZoneId currentZoneId = ZoneId.systemDefault();
        ZonedDateTime start_time0 = last_exercise.getStartTime().atZone(ZoneId.of("UTC") );//默认是用UTC
        LocalDateTime start_time = start_time0.withZoneSameInstant(currentZoneId).toLocalDateTime();//转为SystemDefault
        String category = last_exercise.getCategory().toLowerCase();
        //获取两个时间的差值
        System.out.println("开始时间为"+start_time+"现在时间为"+now);
        int duration = (int) Duration.between(start_time,now).toSeconds();
        //duration是以秒为单位的，需要转为相应的字符串返回给前端
        if(duration<60)
            ans.setTime(String.format("%d秒",duration));
        else if(duration<3600)
            ans.setTime(String.format("%d分%d秒",duration/60,duration%60));
        else
            ans.setTime(String.format("%d小时%d分%d秒",duration/3600,duration%3600/60,duration%60));
        ans.setCategory(last_exercise.getCategory().toLowerCase());
        //获取运动数据
        if(category.equalsIgnoreCase("walking")||category.equalsIgnoreCase("jogging")) {
            runningService.updateRunning(last_exercise.getExerciseId(),longitude,latitude);
            String pace1 = stringRedisTemplate.opsForHash().get(CACHE_RUNNING_KEY + exerciseId, "pace").toString();
            String distance = stringRedisTemplate.opsForHash().get(CACHE_RUNNING_KEY + exerciseId, "distance").toString();
            ans.setDistance(Double.valueOf(distance));
            Integer pace = Double.valueOf(pace1).intValue();
            if (pace < 60)
                ans.setSpeed(String.format("%d秒", pace));
            else
                ans.setSpeed(String.format("%d分%d秒", pace / 60, pace % 60));
        }
        //计算卡路里
        int calorie = CalorieCalculator.getCalorie(category, weight, duration);
        ans.setCalorie(calorie);
        return ans;
    }

}
