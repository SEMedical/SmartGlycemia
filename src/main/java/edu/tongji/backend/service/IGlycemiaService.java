package edu.tongji.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.dto.Chart;
import edu.tongji.backend.dto.CompositeChart;
import edu.tongji.backend.entity.Glycemia;

import java.time.LocalDate;

public interface IGlycemiaService extends IService<Glycemia> {
    Chart showGlycemiaDiagram(String type, String user_id, LocalDate date);
    CompositeChart showGlycemiaHistoryDiagram(String span, String user_id, LocalDate startDate);
    Double getLatestGlycemia(String user_id);
}
