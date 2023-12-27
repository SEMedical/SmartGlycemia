package edu.tongji.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.backend.entity.HeartRate;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HeartRateMapper extends BaseMapper<HeartRate> {
}
