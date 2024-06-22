package edu.tongji.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.backend.entity.Hospital;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface HospitalMapper extends BaseMapper<Hospital> {
    @Select("SELECT MAX(hospital_id) FROM hospital;")
    Integer getMaxId();
    @Select("SELECT EXISTS(SELECT * from hospital where hospital_phone=CONCAT(\"\",#{ contact } ,\"\")" +
            "or hospital_name=CONCAT(\"\", #{ name },\"\") or address=CONCAT(\"\",#{ address },\"\") )")
    Boolean InfoRepeated(String contact,String name,String address);
    @Select("SELECT admin_id from hospital where hospital_id=#{hospitalId};")
    String havaAdministrator(String hospitalId);
    @Update("UPDATE hospital set admin_id=#{adminId} where hospital_id=#{hospitalId};")
    Boolean setAdministrator(String hospitalId,String adminId);
}
