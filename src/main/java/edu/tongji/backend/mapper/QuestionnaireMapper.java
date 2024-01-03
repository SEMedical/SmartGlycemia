package edu.tongji.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.backend.entity.Questionnaire;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface QuestionnaireMapper extends BaseMapper<Questionnaire> {
    @Select("select data from questionnaire where patient_id = #{patientId} and template = #{template} order by questionnaire_id desc limit 1;")
    String selectByPatientIdAndTemplate(Integer patientId, Integer template);
}
