package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.POJO.Chart;
import edu.tongji.backend.entity.Glycemia;
import edu.tongji.backend.mapper.GlycemiaMapper;
import edu.tongji.backend.service.IGlycemiaService;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
        //Just for initialization
        LocalDateTime endTime=LocalDateTime.now();
        if(type=="Realtime") {
            date = LocalDate.now();
        }
        else if(type=="History") {
            if(date==null) {
                chart.setError_code(400);
                throw new RuntimeException("Date is required in history mode");
            }
        }
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
            if(type=="Realtime") {
                Double glycemiaValue=glycemiaMapper.selectByIdAndTime(user_id, startDateTime);
                Map<LocalDateTime,Double> data = new HashMap<>();
                data.put( startDateTime,glycemiaValue);
                res.add(data);
            }
        }
        chart.setData(res);
        chart.setError_code(200);
        return chart;
    }
}
