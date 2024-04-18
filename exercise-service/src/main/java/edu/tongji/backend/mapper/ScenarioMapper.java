package edu.tongji.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.backend.entity.Scenario;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ScenarioMapper extends BaseMapper<Scenario> {
    @Select("select * from scenario where patient_id = #{patientId}")
    Scenario getByPatientId(Integer patientId);

    @Update("update scenario set category = #{category}, duration = #{duration}, calories = #{calories} where patient_id = #{patientId}")
    boolean update(Scenario scenario);
}
