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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static edu.tongji.backend.util.BloomFilterUtil.*;
import static edu.tongji.backend.util.RedisConstants.*;
import static edu.tongji.backend.util.RedisConstants.CACHE_DAILY_GLYCEMIA_TTL;

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
        List<Map<LocalDateTime,Double>> res=new ArrayList<>();
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Long size = stringRedisTemplate.opsForHash().size(CACHE_GLYCEMIA_KEY + user_id + ":" + formattedDate);
        if(size!=null&&size>0){
            Map<Object,Object> glycemiaMap=stringRedisTemplate.opsForHash().entries(CACHE_GLYCEMIA_KEY + user_id + ":" + formattedDate);
            for(Map.Entry<Object,Object> entry:glycemiaMap.entrySet()){
                Map<LocalDateTime,Double> data = new HashMap<>();
                data.put(LocalDateTime.parse(entry.getKey().toString(),formatter),Double.valueOf(entry.getValue().toString()));
                res.add(data);
            }
            chart.setData(res);
            return chart;
        }else{
            stringRedisTemplate.expire(CACHE_GLYCEMIA_KEY + user_id + ":" + formattedDate,
                    (long)(CACHE_GLYCEMIA_TTL*86400+1000*Math.random()),TimeUnit.SECONDS);
        }
        // 遍历时间点，每15分钟一次，直到当前时间
        List<GlycemiaDTO> glycemiaDTOS = glycemiaMapper.selectByIdAndTime(user_id, formattedDate);
        for (GlycemiaDTO glycemiaDTO : glycemiaDTOS) {
            Map<LocalDateTime,Double> data = new HashMap<>();
            data.put(LocalDateTime.parse(glycemiaDTO.getRecordTime(),formatter),glycemiaDTO.getGlycemia());
            res.add(data);
            stringRedisTemplate.opsForHash().put(CACHE_GLYCEMIA_KEY + user_id + ":" + formattedDate,
                    glycemiaDTO.getRecordTime(),glycemiaDTO.getGlycemia().toString());
        }
        chart.setData(res);
        return chart;
    }
    @Override
    public DailyChart showDailyGlycemiaDiagram(String user_id, LocalDate date) {
        DailyChart chart=new DailyChart();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //Just for initialization
        LocalDateTime startDateTime =LocalDateTime.of(date, LocalTime.MIN);
        List<Map<LocalDateTime,Double>> res=new ArrayList<>();
        Integer eu_count=0,hypo_count=0,hyper_count=0;
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Long size = stringRedisTemplate.opsForHash().size(CACHE_DAILY_GLYCEMIA_KEY + user_id + ":" + formattedDate);
        if(size!=null&&size>0){
            Map<Object,Object> glycemiaMap=stringRedisTemplate.opsForHash().entries(CACHE_DAILY_GLYCEMIA_KEY + user_id + ":" + formattedDate);
            for(Map.Entry<Object,Object> entry:glycemiaMap.entrySet()){
                Map<LocalDateTime,Double> data = new HashMap<>();
                try {
                    data.put(LocalDateTime.parse(entry.getKey().toString(), formatter), Double.valueOf(entry.getValue().toString()));
                }catch (DateTimeParseException e){
                    continue;
                }
                res.add(data);
            }
            chart.setLowSta(Double.valueOf(glycemiaMap.get("hypo_count").toString()));
            chart.setNormalSta(Double.valueOf(glycemiaMap.get("eu_count").toString()));
            chart.setHighSta(Double.valueOf(glycemiaMap.get("hyper_count").toString()));
            chart.setEntry(res);
            return chart;
        }else{
            stringRedisTemplate.expire(CACHE_DAILY_GLYCEMIA_KEY + user_id + ":" + formattedDate,
                    (long)(CACHE_DAILY_GLYCEMIA_TTL*86400+1000*Math.random()),TimeUnit.SECONDS);
        }
        // 遍历时间点，每15分钟一次，直到当前时间
        List<GlycemiaDTO> glycemiaDTOS = glycemiaMapper.selectByIdAndTime(user_id, formattedDate);
        for (GlycemiaDTO glycemiaDTO : glycemiaDTOS) {
            Map<LocalDateTime,Double> data = new HashMap<>();
            data.put(LocalDateTime.parse(glycemiaDTO.getRecordTime(),formatter),glycemiaDTO.getGlycemia());
            GlycemiaLevel level=GetGlycemiaLevel(Double.valueOf(userMapper.selectById(user_id).getAge()),startDateTime,glycemiaDTO.getGlycemia());
            if(level==GlycemiaLevel.HYPOGLYCEMIA)
                hypo_count++;
            else if(level==GlycemiaLevel.EUGLYCEMIA)
                eu_count++;
            else
                hyper_count++;
            res.add(data);
            stringRedisTemplate.opsForHash().put(CACHE_DAILY_GLYCEMIA_KEY + user_id + ":" + formattedDate,
                    glycemiaDTO.getRecordTime(),glycemiaDTO.getGlycemia().toString());
        }
        stringRedisTemplate.opsForHash().put(CACHE_DAILY_GLYCEMIA_KEY + user_id + ":" + formattedDate,
                "eu_count",String.valueOf(eu_count*100.0/res.size()));
        stringRedisTemplate.opsForHash().put(CACHE_DAILY_GLYCEMIA_KEY + user_id + ":" + formattedDate,
                "hypo_count",String.valueOf(hypo_count*100.0/res.size()));
        stringRedisTemplate.opsForHash().put(CACHE_DAILY_GLYCEMIA_KEY + user_id + ":" + formattedDate,
                "hyper_count",String.valueOf(hyper_count*100.0/res.size()));

        chart.setLowSta(eu_count*100.0/res.size());
        chart.setNormalSta(hypo_count*100.0/res.size());
        chart.setHighSta(hyper_count*100.0/res.size());
        chart.setEntry(res);
        return chart;
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
            log.debug(startDate.toString());
            String glycemiaJson=stringRedisTemplate.opsForValue().get(CACHE_HISTORY_GLYCEMIA_KEY+user_id+":"+startDate.format(formatter));
            Statistics glycemiaValue=new Statistics();
            if(StrUtil.isNotBlank(glycemiaJson))
                glycemiaValue= JSON.parseObject(glycemiaJson,Statistics.class);
            else{
                glycemiaValue = glycemiaMapper.selectDailyArchive(user_id, startDate.format(formatter));
                //TODO:月度统计
                if (glycemiaValue == null) {
                    log.debug("(Penetration)No data found at" + startDate.format(formatter));
                    startDate = startDate.plusDays(1);
                    continue;
                }
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
            stringRedisTemplate.opsForValue().set(CACHE_LATEST_GLYCEMIA_KEY +user_id,JSON.toJSONString(val));
            stringRedisTemplate.expire(CACHE_LATEST_GLYCEMIA_KEY +user_id, (long) (LATEST_GLYCEMIA_TTL*60+Math.random()*30),TimeUnit.SECONDS);
        }
        String latestDate=val.getRecordTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parsed=null;
        try{
            parsed = LocalDateTime.parse(latestDate,formatter);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(parsed.isAfter(LocalDateTime.now().minus(Duration.ofMinutes(15)))) {
            Double glyValue = val.getGlycemia();
            log.debug(glyValue.toString());
            return glyValue;
        }else if(LocalDateTime.now().getHour()>22||LocalDateTime.now().getHour()<6){
            log.warn("Maybe the user is sleeping,remind him/her to keep track of her glycemia");
            return 0.0;
        }else {
            Double glyValue = val.getGlycemia();
            log.warn("The latest data is out-of-date"+glyValue.toString());
            return glyValue;
        }
    }
}
