package edu.tongji.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.backend.entity.Glycemia;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Mapper
public interface GlycemiaMapper extends BaseMapper<Glycemia> {
    Double selectByIdAndTime(String id, String time);
}
