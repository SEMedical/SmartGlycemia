package edu.tongji.backend.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.dto.*;
import edu.tongji.backend.entity.*;
import edu.tongji.backend.exception.GlycemiaException;
import edu.tongji.backend.mapper.GlycemiaMapper;
import edu.tongji.backend.mapper.ProfileMapper;
import edu.tongji.backend.service.IGlycemiaService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static edu.tongji.backend.util.BloomFilterUtil.*;
import static edu.tongji.backend.util.RedisConstants.*;
@Slf4j
@Service
public class GlycemiaServiceImpl extends ServiceImpl<GlycemiaMapper, Glycemia> implements IGlycemiaService {
    @Autowired
    GlycemiaMapper glycemiaMapper;
    @Autowired
    ProfileMapper userMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Chart showGlycemiaDiagram(String type, String user_id, LocalDate date) {
        Chart chart=new Chart();
        if(type.equals("Realtime"))
            date=LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //Just for initialization
        LocalDateTime endTime=LocalDateTime.now();
        List<Map<LocalDateTime,Double>> res=new ArrayList<>();

        LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.of(0, 0, 0));
        if(type.equals("Realtime")) {
            endTime=LocalDateTime.now();
        }
        else if(type.equals("History")) {
            endTime=startDateTime.plusDays(1);
        }
        // 设置时间间隔为15分钟
        Duration interval = Duration.ofMinutes(15);

        // 遍历时间点，每15分钟一次，直到当前时间
        while (startDateTime.isBefore(endTime)) {
            log.info(startDateTime.toString());
            startDateTime = startDateTime.plus(interval);
            if(!glycemia_bf.mightContain(CACHE_DAILY_GLYCEMIA_KEY+user_id+":"+startDateTime.format(formatter))){
                log.info("No data found at" + startDateTime.format(formatter));
                continue;
            }
            String glycemiaJson=stringRedisTemplate.opsForValue().get(CACHE_GLYCEMIA_KEY+user_id+":"+startDateTime.format(formatter));
            Double glycemiaValue;
            if(StrUtil.isNotBlank(glycemiaJson)){//Cache hit
                glycemiaValue=Double.valueOf(glycemiaJson);
            }else {
                glycemiaValue = glycemiaMapper.selectByIdAndTime(user_id, startDateTime.format(formatter));
                if (glycemiaValue == null) {
                    log.warn("(Penetration!)No data found at" + startDateTime.format(formatter));
                    continue;
                }
                glycemia_bf.put(CACHE_GLYCEMIA_KEY+user_id+":"+startDateTime.format(formatter));
                stringRedisTemplate.opsForValue().set(CACHE_GLYCEMIA_KEY+user_id+":"+startDateTime.format(formatter),glycemiaValue.toString());
                stringRedisTemplate.expire(CACHE_GLYCEMIA_KEY+user_id+":"+startDateTime.format(formatter),
                        (long)(CACHE_GLYCEMIA_TTL*86400+1000*Math.random()),TimeUnit.SECONDS);
            }
            Map<LocalDateTime,Double> data = new HashMap<>();
            data.put(startDateTime,glycemiaValue);
            res.add(data);
        }
        chart.setData(res);
        //chart.setError_code(200);
        return chart;
    }
    @Override
    public DailyChart showDailyGlycemiaDiagram(String user_id, LocalDate date) {
        DailyChart chart=new DailyChart();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //Just for initialization
        LocalDateTime startDateTime =LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endTime=LocalDateTime.of(date, LocalTime.MAX);
        List<Map<LocalDateTime,Double>> res=new ArrayList<>();
        // 设置时间间隔为15分钟
        Duration interval = Duration.ofMinutes(15);
        Integer eu_count=0,hypo_count=0,hyper_count=0;
        // 遍历时间点，每15分钟一次，直到当前时间
        while (startDateTime.isBefore(endTime)) {
            log.info(startDateTime.toString());
            startDateTime = startDateTime.plus(interval);
            if(!daily_glycemia_bf.mightContain(CACHE_DAILY_GLYCEMIA_KEY+user_id+":"+startDateTime.format(formatter))) {
                log.info("No data found around" + startDateTime.format(formatter));
                continue;
            }
            String glycemiaJson=stringRedisTemplate.opsForValue().get(CACHE_DAILY_GLYCEMIA_KEY+user_id+":"+startDateTime.format(formatter));
            Double glycemiaValue;
            if(StrUtil.isNotBlank(glycemiaJson))
                glycemiaValue=Double.valueOf(glycemiaJson);
            else {
                //log.info(startDateTime.format(formatter));

                glycemiaValue = glycemiaMapper.selectByIdAndTime(user_id, startDateTime.format(formatter));
                if (glycemiaValue == null) {
                    log.warn("(Penetration)No data found around" + startDateTime.format(formatter));
                    continue;
                }
                daily_glycemia_bf.put(CACHE_DAILY_GLYCEMIA_KEY+user_id+":"+startDateTime.format(formatter));
                stringRedisTemplate.opsForValue().set(CACHE_DAILY_GLYCEMIA_KEY+user_id+":"+startDateTime.format(formatter),
                        glycemiaValue.toString());
                stringRedisTemplate.expire(CACHE_DAILY_GLYCEMIA_KEY+user_id+":"+startDateTime.format(formatter),
                        (long)(CACHE_DAILY_GLYCEMIA_TTL*86400+1000*Math.random()), TimeUnit.SECONDS);
            }
            Map<LocalDateTime,Double> data = new HashMap<>();
            data.put(startDateTime,glycemiaValue);
            GlycemiaLevel level=GetGlycemiaLevel(Double.valueOf(userMapper.selectById(user_id).getAge()),startDateTime,glycemiaValue);
            if(level==GlycemiaLevel.HYPOGLYCEMIA)
                hypo_count++;
            else if(level==GlycemiaLevel.EUGLYCEMIA)
                eu_count++;
            else
                hyper_count++;
            res.add(data);
        }
        chart.setLowSta(eu_count*100.0/res.size());
        chart.setNormalSta(hypo_count*100.0/res.size());
        chart.setHighSta(hyper_count*100.0/res.size());
        chart.setEntry(res);
        return chart;
    }
    private static boolean isInSameInterval(LocalDateTime time1, LocalDateTime time2) {
        long minutesFromMidnight1 = ChronoUnit.MINUTES.between(LocalDateTime.of(time1.toLocalDate(), LocalTime.MIDNIGHT), time1);
        long minutesFromMidnight2 = ChronoUnit.MINUTES.between(LocalDateTime.of(time2.toLocalDate(), LocalTime.MIDNIGHT), time2);

        int interval1 = (int) (minutesFromMidnight1 / 15);
        int interval2 = (int) (minutesFromMidnight2 / 15);

        return interval1 == interval2;
    }
    public void Init_GlycemiaHistoryDiagram(){
        QueryWrapper<Glycemia> queryWrapper = new QueryWrapper<>();
        List<Glycemia> exercises = glycemiaMapper.selectList(queryWrapper);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        exercises.forEach(element->history_glycemia_bf.put(
                CACHE_HISTORY_GLYCEMIA_KEY+element.getPatientId()+":"+
                        element.getRecordTime().toLocalDateTime().format(formatter)));
    }
    public void Init_GlycemiaDiagram(){
        QueryWrapper<Glycemia> queryWrapper = new QueryWrapper<>();
        List<Glycemia> glycemias = glycemiaMapper.selectList(queryWrapper);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        glycemias.forEach(element->glycemia_bf.put(
                CACHE_GLYCEMIA_KEY+element.getPatientId()+":"+
                        element.getRecordTime().toLocalDateTime().format(formatter)));
    }
    public void Init_DailyGlycemiaDiagram(){
        QueryWrapper<Glycemia> queryWrapper = new QueryWrapper<>();
        List<Glycemia> glycemias = glycemiaMapper.selectList(queryWrapper);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        glycemias.forEach(element-> {
            LocalDateTime dateTime = element.getRecordTime().toLocalDateTime().minusHours(8);
            int minute = dateTime.getMinute();
            int nearestMultipleOf15 = (int) Math.floor(minute / 15.0) * 15;
            dateTime=dateTime.withMinute(nearestMultipleOf15).withSecond(0);
            daily_glycemia_bf.put(
                    CACHE_DAILY_GLYCEMIA_KEY + element.getPatientId() + ":" +
                            dateTime.format(formatter));
        });
    }
    public void Init_LatestGlycemiaDiagram(){
        QueryWrapper<Glycemia> queryWrapper = new QueryWrapper<>();
        List<Glycemia> exercises = glycemiaMapper.selectList(queryWrapper);
        exercises.forEach(element->latest_glycemia_bf.put(
                CACHE_LATEST_GLYCEMIA_KEY+element.getPatientId()));
    }
    @Override
    public CompositeChart showGlycemiaHistoryDiagram(String span, String user_id, LocalDate startDate) {
        CompositeChart chart = new CompositeChart();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //Just for initialization
        LocalDate endTime = LocalDate.now();
        List<Map<LocalDate, StatisticsCondensed>> Res = new ArrayList<>();
        //自动发现当前日期所在的周/月/日
        if (span.equals("Week")) {
            endTime = startDate.with(DayOfWeek.SUNDAY);
            startDate = startDate.with(DayOfWeek.MONDAY);
        } else if (span.equals("Month")) {
            endTime = startDate.withDayOfMonth(startDate.lengthOfMonth());
            startDate = startDate.withDayOfMonth(1);
        }else
            endTime = startDate.plusDays(1);
        //entoll:总普通血糖比例，hypotoll:总低血糖比例，hypertoll:总高血糖比例
        Double eutoll=0.0,hypotoll=0.0,hypertoll=0.0;
        // 遍历时间点，每1天一次，直到当前时间
        while (startDate.isBefore(endTime)) {
            log.info(startDate.toString());
            if(!history_glycemia_bf.mightContain(CACHE_HISTORY_GLYCEMIA_KEY+user_id+":"+startDate.format(formatter))){
                log.info("No data found at" + startDate.format(formatter));
                startDate = startDate.plusDays(1);
                continue;
            }
            String glycemiaJson=stringRedisTemplate.opsForValue().get(CACHE_HISTORY_GLYCEMIA_KEY+user_id+":"+startDate.format(formatter));
            Statistics glycemiaValue=new Statistics();
            if(StrUtil.isNotBlank(glycemiaJson))
                glycemiaValue= JSON.parseObject(glycemiaJson,Statistics.class);
            else{
                glycemiaValue = glycemiaMapper.selectDailyArchive(user_id, startDate.format(formatter));
                //TODO:月度统计
                if (glycemiaValue == null) {
                    log.warn("(Penetration)No data found at" + startDate.format(formatter));
                    startDate = startDate.plusDays(1);
                    continue;
                }
                history_glycemia_bf.put(CACHE_HISTORY_GLYCEMIA_KEY+user_id+":"+startDate.format(formatter));
                stringRedisTemplate.opsForValue().set(CACHE_HISTORY_GLYCEMIA_KEY+user_id+":"+startDate.format(formatter),JSON.toJSONString(glycemiaValue));
                stringRedisTemplate.expire(CACHE_HISTORY_GLYCEMIA_KEY+user_id+":"+startDate.format(formatter),
                        (long)(CACHE_HISTORY_GLYCEMIA_TTL*86400+1000*Math.random()),TimeUnit.SECONDS);
            }
            Map<LocalDate,StatisticsCondensed> data = new HashMap<>();
            // 计算总的血糖比例
            StatisticsCondensed glycemiaCondensed = new StatisticsCondensed();
            glycemiaCondensed.setTime(LocalDate.parse(glycemiaValue.getTime()));
            glycemiaCondensed.setMaxValue(glycemiaValue.getMaxValue());
            glycemiaCondensed.setMinValue(glycemiaValue.getMinValue());
            eutoll+=glycemiaValue.getEuGlycemiaPercentage();
            hypotoll+=glycemiaValue.getHypoglycemiaPercentage();
            hypertoll+=glycemiaValue.getHyperglycemiaPercentage();
            data.put(startDate,glycemiaCondensed);
            Res.add(data);
            startDate = startDate.plusDays(1);
        }
        eutoll/=Res.size();
        hypotoll/=Res.size();
        hypertoll/=Res.size();
        chart.setData(Res);
        chart.setEuGlycemiaPercentage(eutoll);
        chart.setHypoglycemiaPercentage(hypotoll);
        chart.setHyperglycemiaPercentage(hypertoll);

        return chart;
    }
    @Override
    public GlycemiaLevel GetGlycemiaLevel(Double age,LocalDateTime date,Double data){
        Double HYPER_THRESHOLD,EU_THRESHOLD,AFTERLUNCH_HYPER_THRESHOLD,AFTERDINNER_HYPER_THRESHOLD;
        AFTERLUNCH_HYPER_THRESHOLD = 8.325;
        AFTERDINNER_HYPER_THRESHOLD = 7.215;
        if (age > 60) {
            HYPER_THRESHOLD = 8.991;
            EU_THRESHOLD = 6.993;
            AFTERLUNCH_HYPER_THRESHOLD = 12.654;
            AFTERDINNER_HYPER_THRESHOLD = 10.989;
        } else if (age < 18) {
            HYPER_THRESHOLD = 6.049;
            EU_THRESHOLD = 4.440;
        } else {
            HYPER_THRESHOLD =6.993;
            EU_THRESHOLD = 6.105;
        }
        Boolean AfterDinner=(date.getHour()>18&&date.getHour()<19);
        Boolean AfterLunch=(date.getHour()>12&&date.getHour()<14);
        if(data<EU_THRESHOLD)//RGBA for Red
            return GlycemiaLevel.HYPOGLYCEMIA;
        else if(data<HYPER_THRESHOLD){
            return GlycemiaLevel.EUGLYCEMIA;
        }else if(AfterLunch&&data>AFTERLUNCH_HYPER_THRESHOLD){
            return GlycemiaLevel.HYPERGLYCEMIA;
        }else if(AfterDinner&&data>AFTERDINNER_HYPER_THRESHOLD)
            return GlycemiaLevel.HYPERGLYCEMIA;
        else if(AfterDinner ||AfterLunch){
            return GlycemiaLevel.EUGLYCEMIA;
        }else{
            if(data>HYPER_THRESHOLD)
                return GlycemiaLevel.HYPERGLYCEMIA;
        }
        return GlycemiaLevel.UNKNOWN;
    }
    //实时的标准是15分钟以内
    @Override
    public Double getLatestGlycemia(String user_id) {
        if(!latest_glycemia_bf.mightContain(CACHE_LATEST_GLYCEMIA_KEY +user_id))
            throw new GlycemiaException("All the glycemia data is not accessible!");
        String valjson=stringRedisTemplate.opsForValue().get(CACHE_LATEST_GLYCEMIA_KEY +user_id);
        GlycemiaDTO val;
        if(StrUtil.isNotBlank(valjson)) {
            val=JSON.parseObject(valjson, GlycemiaDTO.class);
        }else {
            val = glycemiaMapper.getRealtimeGlycemia(user_id);
            if (val == null) {
                //TODO:CACHE PENETRATION!
                throw new GlycemiaException("All the glycemia data is not accessible(Cache Penetration)!");
            }
            latest_glycemia_bf.put(CACHE_LATEST_GLYCEMIA_KEY +user_id);
            stringRedisTemplate.opsForValue().set(CACHE_LATEST_GLYCEMIA_KEY +user_id,JSON.toJSONString(val));
            stringRedisTemplate.expire(CACHE_LATEST_GLYCEMIA_KEY +user_id, (long) (LATEST_GLYCEMIA_TTL*60+Math.random()*30),TimeUnit.SECONDS);
        }
        String latestDate=val.getRecordTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parsed = LocalDateTime.parse(latestDate,formatter);
        if(parsed.isAfter(LocalDateTime.now().minus(Duration.ofMinutes(15)))) {
            Double glyValue = val.getGlycemia();
            log.info(glyValue.toString());
            return glyValue;
        }else if(LocalDateTime.now().getHour()>22||LocalDateTime.now().getHour()<6){
            throw new GlycemiaException("Maybe the user is sleeping,remind him/her to keep track of her glycemia");
        }else
            throw new GlycemiaException("Latest data is not accessible!");
    }
}
