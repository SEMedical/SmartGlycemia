package edu.tongji.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.entity.Chart;
import edu.tongji.backend.entity.Glycemia;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;

public interface IGlycemiaService extends IService<Glycemia> {
    Chart showGlycemiaDiagram(String type, String user_id, LocalDate date);
}
