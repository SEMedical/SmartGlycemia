package edu.tongji.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.backend.entity.Profile;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProfileMapper extends BaseMapper<Profile> {
    Profile getByPatientIdProfile(Integer patient_id);

    @Update("UPDATE profile SET gender = #{gender}, age = #{age}, weight = #{weight}, height = #{height}, type = #{type}, diagnosed_year = #{diagnosedYear}, family_history = #{familyHistory} WHERE patient_id = #{patientId}")
    boolean update(Profile profile);
}
