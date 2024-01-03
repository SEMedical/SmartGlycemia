package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.dto.*;
import edu.tongji.backend.entity.*;
import edu.tongji.backend.exception.GlycemiaException;
import edu.tongji.backend.mapper.GlycemiaMapper;
import edu.tongji.backend.mapper.ProfileMapper;
import edu.tongji.backend.service.IGlycemiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GlycemiaServiceImpl extends ServiceImpl<GlycemiaMapper, Glycemia> implements IGlycemiaService {
    @Autowired
    GlycemiaMapper glycemiaMapper;
    @Autowired
    ProfileMapper userMapper;
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
            System.out.println(startDateTime);
            startDateTime = startDateTime.plus(interval);
            Double glycemiaValue=glycemiaMapper.selectByIdAndTime(user_id, startDateTime.format(formatter));
            if(glycemiaValue==null) {
                System.out.println("No data found at" + startDateTime.format(formatter));
                continue;
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
            System.out.println(startDateTime);
            startDateTime = startDateTime.plus(interval);
            Double glycemiaValue=glycemiaMapper.selectByIdAndTime(user_id, startDateTime.format(formatter));
            if(glycemiaValue==null) {
                System.out.println("No data found around" + startDateTime.format(formatter));
                continue;
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
            System.out.println(startDate);

            Statistics glycemiaValue=new Statistics();
            glycemiaValue = glycemiaMapper.selectDailyArchive(user_id, startDate.format(formatter) );
            //TODO:月度统计
            if (glycemiaValue == null) {
                System.out.println("No data found at" + startDate.format(formatter));
                startDate = startDate.plusDays(1);
                continue;
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
        GlycemiaDTO val=glycemiaMapper.getRealtimeGlycemia(user_id);
        if(val==null)
            throw new GlycemiaException("All the glycemia data is not accessible!");
        String latestDate=val.getRecordTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parsed = LocalDateTime.parse(latestDate,formatter);
        if(parsed.isAfter(LocalDateTime.now().minus(Duration.ofMinutes(15)))) {
            Double glyValue = val.getGlycemia();
            System.out.println(glyValue);
            return glyValue;
        }else if(LocalDateTime.now().getHour()>22||LocalDateTime.now().getHour()<6){
            throw new GlycemiaException("Maybe the user is sleeping,remind him/her to keep track of her glycemia");
        }else
            throw new GlycemiaException("Latest data is not accessible!");
    }
}
