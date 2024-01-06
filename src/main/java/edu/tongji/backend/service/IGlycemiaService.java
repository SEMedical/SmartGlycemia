package edu.tongji.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.dto.Chart;
import edu.tongji.backend.dto.CompositeChart;
import edu.tongji.backend.dto.DailyChart;
import edu.tongji.backend.dto.GlycemiaLevel;
import edu.tongji.backend.entity.Glycemia;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface IGlycemiaService extends IService<Glycemia> {
    Chart showGlycemiaDiagram(String type, String user_id, LocalDate date);
    public DailyChart showDailyGlycemiaDiagram(String user_id, LocalDate date);
    public GlycemiaLevel GetGlycemiaLevel(Double age, LocalDateTime date, Double data);
    CompositeChart showGlycemiaHistoryDiagram(String span, String user_id, LocalDate startDate);
    Double getLatestGlycemia(String user_id);
}
