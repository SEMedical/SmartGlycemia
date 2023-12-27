package edu.tongji.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.entity.Chart;
import edu.tongji.backend.entity.CompositeChart;
import edu.tongji.backend.entity.Glycemia;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

public interface IGlycemiaService extends IService<Glycemia> {
    Chart showGlycemiaDiagram(String type, String user_id, LocalDate date);
    CompositeChart showGlycemiaHistoryDiagram(String span, String user_id, LocalDate startDate);
}
