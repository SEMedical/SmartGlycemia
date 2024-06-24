package edu.tongji.backend.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.emory.mathcs.backport.java.util.Collections;
import edu.tongji.backend.dto.*;
import edu.tongji.backend.entity.*;
import edu.tongji.backend.exception.GlycemiaException;
import edu.tongji.backend.mapper.GlycemiaMapper;
import edu.tongji.backend.mapper.ProfileMapper;
import edu.tongji.backend.service.IGlycemiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static edu.tongji.backend.util.RedisConstants.*;

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
        Chart chart = new Chart();
        if (type.equals("Realtime"))
            date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //Just for initialization
        List<Map<LocalDateTime, Double>> res = new ArrayList<>();
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String startTime = String.valueOf(Timestamp.valueOf(date.atStartOfDay().plusHours(8)).getTime());
        String endTime = String.valueOf(Timestamp.valueOf(date.atStartOfDay().plusDays(1).plusHours(8)).getTime());
        List<List> lists = stringRedisTemplate.execute(TS_SCRIPT2, Collections.singletonList(CACHE_GLYCEMIA_KEY + user_id),
                startTime, endTime);
        int TSSize = lists.size();
        if(TSSize!=0) {
            for (List list : lists) {
                LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(String.valueOf(list.get(0)))), ZoneId.systemDefault());
                Double glycemiaValue = Double.valueOf((String) list.get(1));
                Map<LocalDateTime, Double> data = new HashMap<>();
                data.put(localDateTime, glycemiaValue);
                res.add(data);
            }
            chart.setData(res);
            return chart;
        }
        Integer eu_count=0,hypo_count=0,hyper_count=0;
        Double min_val= Double.MAX_VALUE,max_val=Double.MIN_VALUE;
        Double avg=0.0;
        List<GlycemiaDTO> glycemiaDTOS = glycemiaMapper.selectByIdAndTime(user_id, formattedDate);
        for (GlycemiaDTO glycemiaDTO : glycemiaDTOS) {
            Map<LocalDateTime,Double> data = new HashMap<>();
            data.put(LocalDateTime.parse(glycemiaDTO.getRecordTime(),formatter),glycemiaDTO.getGlycemia());
            res.add(data);
            GlycemiaLevel level=GetGlycemiaLevel(Double.valueOf(userMapper.selectById(user_id).getAge()),date.atStartOfDay().plusHours(8),glycemiaDTO.getGlycemia());
            if(level==GlycemiaLevel.HYPOGLYCEMIA)
                hypo_count++;
            else if(level==GlycemiaLevel.EUGLYCEMIA)
                eu_count++;
            else
                hyper_count++;
            if(glycemiaDTO.getGlycemia()<min_val)
                min_val=glycemiaDTO.getGlycemia();
            if(glycemiaDTO.getGlycemia()>max_val)
                max_val=glycemiaDTO.getGlycemia();
            if(glycemiaDTO.getGlycemia()<min_val)
                min_val=glycemiaDTO.getGlycemia();
            if(glycemiaDTO.getGlycemia()>max_val)
                max_val=glycemiaDTO.getGlycemia();
            avg+=glycemiaDTO.getGlycemia();
            //Get Timestamp
            Timestamp timestamp = Timestamp.valueOf(glycemiaDTO.getRecordTime());
            stringRedisTemplate.execute(TS_SCRIPT,Collections.singletonList(CACHE_GLYCEMIA_KEY+user_id),String.valueOf(timestamp.getTime()),glycemiaDTO.getGlycemia().toString());
        }
        avg=avg/res.size();
        stringRedisTemplate.execute(TS_SCRIPT,Collections.singletonList(CACHE_GLYCEMIA_KEY+user_id+":avg"),
                startTime,avg.toString());
        stringRedisTemplate.execute(TS_SCRIPT,Collections.singletonList(CACHE_GLYCEMIA_KEY+user_id+":max"),
                startTime,max_val.toString());
        stringRedisTemplate.execute(TS_SCRIPT,Collections.singletonList(CACHE_GLYCEMIA_KEY+user_id+":min"),
                startTime,min_val.toString());
        stringRedisTemplate.execute(TS_SCRIPT,Collections.singletonList(CACHE_GLYCEMIA_KEY+user_id+":hyper"),
                startTime,hyper_count.toString());
        stringRedisTemplate.execute(TS_SCRIPT,Collections.singletonList(CACHE_GLYCEMIA_KEY+user_id+":eu"),
                startTime,eu_count.toString());
        stringRedisTemplate.execute(TS_SCRIPT,Collections.singletonList(CACHE_GLYCEMIA_KEY+user_id+":hypo"),
                startTime,hypo_count.toString());
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
        Double min_val= Double.MAX_VALUE,max_val=Double.MIN_VALUE;
        Double avg=0.0;
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String startTime = String.valueOf(Timestamp.valueOf(date.atStartOfDay().plusHours(8)).getTime());
        String endTime = String.valueOf(Timestamp.valueOf(date.atStartOfDay().plusDays(1).plusHours(8)).getTime());
        List<List> lists = stringRedisTemplate.execute(TS_SCRIPT2, Collections.singletonList(CACHE_GLYCEMIA_KEY + user_id),
                startTime, endTime);
        int TSSize = lists.size();
        if(TSSize!=0) {
            for (List list : lists) {
                LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(String.valueOf(list.get(0)))), ZoneId.systemDefault());
                Double glycemiaValue = Double.valueOf((String) list.get(1));
                Map<LocalDateTime, Double> data = new HashMap<>();
                data.put(localDateTime, glycemiaValue);
                res.add(data);
            }
            List<List> hypo_list = stringRedisTemplate.execute(TS_SCRIPT3, Collections.singletonList(CACHE_GLYCEMIA_KEY + user_id+":hypo"),
                    startTime);
            if(hypo_list!=null)
                for (List list : hypo_list) {
                    chart.setLowSta(Double.valueOf(list.get(1).toString()));
                }
            List<List> eu_list = stringRedisTemplate.execute(TS_SCRIPT3, Collections.singletonList(CACHE_GLYCEMIA_KEY + user_id+":eu"),
                    startTime);
            if(eu_list!=null)
            for (List list : eu_list) {
                chart.setNormalSta(Double.valueOf(list.get(1).toString()));
            }
            List<List> hyper_list = stringRedisTemplate.execute(TS_SCRIPT3, Collections.singletonList(CACHE_GLYCEMIA_KEY + user_id+":hyper"),
                    startTime);
            if(hyper_list!=null)
            for (List list : hyper_list) {
                chart.setHighSta(Double.valueOf(list.get(1).toString()));
            }
            chart.setEntry(res);
            return chart;
        }
        // 遍历时间点，每15分钟一次，直到当前时间
        List<GlycemiaDTO> glycemiaDTOS = glycemiaMapper.selectByIdAndTime(user_id, formattedDate);
        for (GlycemiaDTO glycemiaDTO : glycemiaDTOS) {
            Map<LocalDateTime,Double> data = new HashMap<>();
            data.put(LocalDateTime.parse(glycemiaDTO.getRecordTime(),formatter),glycemiaDTO.getGlycemia());
            res.add(data);
            GlycemiaLevel level=GetGlycemiaLevel(Double.valueOf(userMapper.selectById(user_id).getAge()),startDateTime,glycemiaDTO.getGlycemia());
            if(level==GlycemiaLevel.HYPOGLYCEMIA)
                hypo_count++;
            else if(level==GlycemiaLevel.EUGLYCEMIA)
                eu_count++;
            else
                hyper_count++;
            if(glycemiaDTO.getGlycemia()<min_val)
                min_val=glycemiaDTO.getGlycemia();
            if(glycemiaDTO.getGlycemia()>max_val)
                max_val=glycemiaDTO.getGlycemia();
            if(glycemiaDTO.getGlycemia()<min_val)
                min_val=glycemiaDTO.getGlycemia();
            if(glycemiaDTO.getGlycemia()>max_val)
                max_val=glycemiaDTO.getGlycemia();
            avg+=glycemiaDTO.getGlycemia();
            Timestamp timestamp = Timestamp.valueOf(glycemiaDTO.getRecordTime());
            stringRedisTemplate.execute(TS_SCRIPT,Collections.singletonList(CACHE_GLYCEMIA_KEY+user_id),String.valueOf(timestamp.getTime()),glycemiaDTO.getGlycemia().toString());
        }
        if(res.size()!=0)
            avg=avg/res.size();
        stringRedisTemplate.execute(TS_SCRIPT, Collections.singletonList(CACHE_GLYCEMIA_KEY + user_id + ":avg"),
                startTime, avg.toString());
        stringRedisTemplate.execute(TS_SCRIPT, Collections.singletonList(CACHE_GLYCEMIA_KEY + user_id + ":max"),
                startTime, max_val.toString());
        stringRedisTemplate.execute(TS_SCRIPT,Collections.singletonList(CACHE_GLYCEMIA_KEY+user_id+":min"),
                startTime,min_val.toString());
        stringRedisTemplate.execute(TS_SCRIPT,Collections.singletonList(CACHE_GLYCEMIA_KEY+user_id+":hyper"),
                startTime,hyper_count.toString());
        stringRedisTemplate.execute(TS_SCRIPT,Collections.singletonList(CACHE_GLYCEMIA_KEY+user_id+":eu"),
                startTime,eu_count.toString());
        stringRedisTemplate.execute(TS_SCRIPT,Collections.singletonList(CACHE_GLYCEMIA_KEY+user_id+":hypo"),
                startTime,hypo_count.toString());
        chart.setLowSta(eu_count*100.0/res.size());
        chart.setNormalSta(hypo_count*100.0/res.size());
        chart.setHighSta(hyper_count*100.0/res.size());
        chart.setEntry(res);
        return chart;
    }
    private static final DefaultRedisScript<List> TS_SCRIPT3;
    static {
        TS_SCRIPT3=new DefaultRedisScript<>();
        TS_SCRIPT3.setLocation(new ClassPathResource("ts3.lua"));
        TS_SCRIPT3.setResultType(List.class);
    }
    private static final DefaultRedisScript<String> TS_SCRIPT;
    static {
        TS_SCRIPT=new DefaultRedisScript<>();
        TS_SCRIPT.setLocation(new ClassPathResource("ts.lua"));
        TS_SCRIPT.setResultType(String.class);
    }
    private static final DefaultRedisScript<List> TS_SCRIPT2;
    static {
        TS_SCRIPT2=new DefaultRedisScript<>();
        TS_SCRIPT2.setLocation(new ClassPathResource("ts2.lua"));
        TS_SCRIPT2.setResultType(List.class);
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
        //Trimmed
        if(endTime.isAfter(LocalDate.now())){
            endTime=LocalDate.now();
        }
        //entoll:总普通血糖比例，hypotoll:总低血糖比例，hypertoll:总高血糖比例
        Double eutoll=0.0,hypotoll=0.0,hypertoll=0.0;
        LocalDate originalStartDate=startDate;
        // 遍历时间点，每1天一次，直到当前时间
        while (startDate.isBefore(endTime)) {
            log.debug(startDate.toString());
            String startTime = String.valueOf(Timestamp.valueOf(startDate.atStartOfDay().plusHours(8)).getTime());
            String endTimeStr=String.valueOf(Timestamp.valueOf(startDate.atStartOfDay().plusHours(32)).getTime());
            List<List> lists = stringRedisTemplate.execute(TS_SCRIPT2, Collections.singletonList(CACHE_GLYCEMIA_KEY + user_id),
                  startTime, endTimeStr);
            int size = lists.size();
            if(size==0){
                stringRedisTemplate.execute(TS_SCRIPT,Collections.singletonList(CACHE_GLYCEMIA_KEY+user_id),startTime,"-1");
                stringRedisTemplate.execute(TS_SCRIPT,Collections.singletonList(CACHE_GLYCEMIA_KEY+user_id+":hyper"),startTime,"0");
                stringRedisTemplate.execute(TS_SCRIPT,Collections.singletonList(CACHE_GLYCEMIA_KEY+user_id+":hypo"),startTime,"0");
                stringRedisTemplate.execute(TS_SCRIPT,Collections.singletonList(CACHE_GLYCEMIA_KEY+user_id+":eu"),startTime,"0");
                stringRedisTemplate.execute(TS_SCRIPT,Collections.singletonList(CACHE_GLYCEMIA_KEY+user_id+":avg"),startTime,"0");
                stringRedisTemplate.execute(TS_SCRIPT,Collections.singletonList(CACHE_GLYCEMIA_KEY+user_id+":min"),startTime,"0");
                stringRedisTemplate.execute(TS_SCRIPT,Collections.singletonList(CACHE_GLYCEMIA_KEY+user_id+":max"),startTime,"0");
                showDailyGlycemiaDiagram(user_id,startDate);
            }
            startDate = startDate.plusDays(1);
        }
        startDate=originalStartDate;
        while (startDate.isBefore(endTime)){
            log.debug(startDate.toString());
            String startTime = String.valueOf(Timestamp.valueOf(startDate.atStartOfDay().plusHours(8)).getTime());
            String endTimeStr=String.valueOf(Timestamp.valueOf(endTime.atStartOfDay().plusHours(8)).getTime());
            //String glycemiaJson=stringRedisTemplate.opsForValue().get(CACHE_HISTORY_GLYCEMIA_KEY+user_id+":"+startDate.format(formatter));
            Statistics glycemiaValue=new Statistics();
            Map<LocalDate,StatisticsCondensed> data = new HashMap<>();
            // 计算总的血糖比例
            List<List> hypo_list = stringRedisTemplate.execute(TS_SCRIPT3, Collections.singletonList(CACHE_GLYCEMIA_KEY + user_id+":hypo"),
                    startTime,endTimeStr);
            List<List> eu_list = stringRedisTemplate.execute(TS_SCRIPT3, Collections.singletonList(CACHE_GLYCEMIA_KEY + user_id+":eu"),
                    startTime,endTimeStr);
            List<List> hyper_list = stringRedisTemplate.execute(TS_SCRIPT3, Collections.singletonList(CACHE_GLYCEMIA_KEY + user_id+":hyper"),
                    startTime,endTimeStr);
            List<List> min_list = stringRedisTemplate.execute(TS_SCRIPT3, Collections.singletonList(CACHE_GLYCEMIA_KEY + user_id+":min"),
                    startTime,endTimeStr);
            List<List> max_list = stringRedisTemplate.execute(TS_SCRIPT3, Collections.singletonList(CACHE_GLYCEMIA_KEY + user_id+":max"),
                    startTime,endTimeStr);
            List<List> avg_list = stringRedisTemplate.execute(TS_SCRIPT3, Collections.singletonList(CACHE_GLYCEMIA_KEY + user_id+":avg"),
                    startTime,endTimeStr);
            Integer listLength=max_list.size();
            for(int i=0;i<listLength;i++){
                glycemiaValue.setHypoglycemiaPercentage(Double.valueOf(hypo_list.get(i).get(1).toString()));
                glycemiaValue.setEuGlycemiaPercentage(Double.valueOf(eu_list.get(i).get(1).toString()));
                glycemiaValue.setHyperglycemiaPercentage(Double.valueOf(hyper_list.get(i).get(1).toString()));
                glycemiaValue.setMinValue(Double.valueOf(min_list.get(i).get(1).toString()));
                glycemiaValue.setMaxValue(Double.valueOf(max_list.get(i).get(1).toString()));
                glycemiaValue.setAverageValue(Double.valueOf(avg_list.get(i).get(1).toString()));
                glycemiaValue.setTime(startTime);
                startTime=String.valueOf(Long.valueOf(startTime)+86400000);
                StatisticsCondensed glycemiaCondensed = new StatisticsCondensed();
                glycemiaCondensed.setTime(Instant.ofEpochMilli(Long.valueOf(startTime))
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime().toLocalDate());
                glycemiaCondensed.setMaxValue(glycemiaValue.getMaxValue());
                glycemiaCondensed.setMinValue(glycemiaValue.getMinValue());
                if(glycemiaValue.getEuGlycemiaPercentage()!=null)
                    eutoll+=glycemiaValue.getEuGlycemiaPercentage();
                if(glycemiaValue.getHypoglycemiaPercentage()!=null)
                    hypotoll+=glycemiaValue.getHypoglycemiaPercentage();
                if(glycemiaValue.getHyperglycemiaPercentage()!=null)
                    hypertoll+=glycemiaValue.getHyperglycemiaPercentage();
                data.put(startDate,glycemiaCondensed);
                Res.add(data);
            }
            startDate = endTime;
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
