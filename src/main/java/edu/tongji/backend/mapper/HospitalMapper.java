package edu.tongji.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.backend.dto.HospitalDTO;
import edu.tongji.backend.entity.HeartRate;
import edu.tongji.backend.entity.Hospital;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HospitalMapper extends BaseMapper<Hospital> {
    List<HospitalDTO> getAllGEO();
}
