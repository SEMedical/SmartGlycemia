package edu.tongji.backend.mapper;

import edu.tongji.backend.entity.Complication;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ComplicationMapper extends BaseMapper<Complication>{
    @Select("SELECT symptom FROM complication WHERE patient_id = #{patient_id}")
    List<String> getByPatientId(Integer patient_id);
}
