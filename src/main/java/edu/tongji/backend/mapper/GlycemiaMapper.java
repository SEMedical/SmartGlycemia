package edu.tongji.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.backend.entity.Glycemia;
import edu.tongji.backend.dto.GlycemiaDTO;
import edu.tongji.backend.dto.Statistics;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;

@Mapper
public interface GlycemiaMapper extends BaseMapper<Glycemia> {
    Double selectByIdAndTime(String id, String time);
    Statistics selectDailyArchive(String userId, String Date);
    GlycemiaDTO getRealtimeGlycemia(String userId);
}
