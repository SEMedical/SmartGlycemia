package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.entity.Chart;
import edu.tongji.backend.entity.CompositeChart;
import edu.tongji.backend.entity.Glycemia;
import edu.tongji.backend.entity.Statistics;
import edu.tongji.backend.mapper.GlycemiaMapper;
import edu.tongji.backend.service.IGlycemiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GlycemiaServiceImpl extends ServiceImpl<GlycemiaMapper, Glycemia> implements IGlycemiaService {
    @Autowired
    GlycemiaMapper glycemiaMapper;

    @Override
    public Chart showGlycemiaDiagram(String type, String user_id, LocalDate date) {
        Chart chart=new Chart();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        //Just for initialization
        LocalDateTime endTime=LocalDateTime.now();
        List<Map<LocalDateTime,Double>> res=new ArrayList<>();

        LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.of(0, 0, 0));
        if(type=="Realtime") {
            endTime=LocalDateTime.now();
        }
        else if(type=="History") {
            endTime=startDateTime.plus(Duration.ofDays(1));
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
    public CompositeChart showGlycemiaHistoryDiagram(String span, String user_id, LocalDate startDate) {
        CompositeChart chart = new CompositeChart();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        //Just for initialization
        LocalDateTime endTime = LocalDateTime.now();
        List<Map<LocalDate, Statistics>> Res = new ArrayList<>();
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.of(0, 0, 0));
        if (span == "Week") {
            endTime = startDateTime.plus(Duration.ofDays(7));
        } else if (span == "Month") {
            endTime = startDateTime.plus(Duration.ofDays(30));
        }

        // 遍历时间点，每15分钟一次，直到当前时间
        while (startDateTime.isBefore(endTime)) {
            System.out.println(startDateTime);
            startDateTime = startDateTime.plus(Duration.ofDays(1));
            Statistics glycemiaValue = glycemiaMapper.selectWeeklyArchive(user_id, startDateTime.format(formatter), span);
            if (glycemiaValue == null) {
                System.out.println("No data found at" + startDateTime.format(formatter));
                continue;
            }
            Map<LocalDate,Statistics> data = new HashMap<>();
            data.put(startDateTime.toLocalDate(),glycemiaValue);
            Res.add(data);
        }
        chart.setData(Res);
        //chart.setError_code(200);

        return chart;
    }

    @Override
    public CompositeChart showGlycemiaHistoryDiagram(String span, String user_id, LocalDate startDate) {
        CompositeChart chart = new CompositeChart();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        //Just for initialization
        LocalDateTime endTime = LocalDateTime.now();
        List<Map<LocalDate, Statistics>> Res = new ArrayList<>();
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.of(0, 0, 0));
        if (span == "Week") {
            endTime = startDateTime.plus(Duration.ofDays(7));
        } else if (span == "Month") {
            endTime = startDateTime.plus(Duration.ofDays(30));
        }

        // 遍历时间点，每15分钟一次，直到当前时间
        while (startDateTime.isBefore(endTime)) {
            System.out.println(startDateTime);
            startDateTime = startDateTime.plus(Duration.ofDays(1));
            Statistics glycemiaValue = glycemiaMapper.selectWeeklyArchive(user_id, startDateTime.format(formatter), span);
            if (glycemiaValue == null) {
                System.out.println("No data found at" + startDateTime.format(formatter));
                continue;
            }
            Map<LocalDate,Statistics> data = new HashMap<>();
            data.put(startDateTime.toLocalDate(),glycemiaValue);
            Res.add(data);
        }
        chart.setData(Res);
        //chart.setError_code(200);

        return chart;
    }
}
