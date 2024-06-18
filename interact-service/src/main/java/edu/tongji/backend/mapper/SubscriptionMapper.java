package edu.tongji.backend.mapper;

import edu.tongji.backend.dto.SinglePatientInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SubscriptionMapper {
    @Insert("insert into subscription(doctor_id,patient_id)" +
            " values(#{doctor_id},#{patient_id})")
    Boolean addSubscription(String doctor_id, String patient_id);

    @Delete("delete from subscription where " +
            " doctor_id=#{doctor_id} and patient_id=#{patient_id}")
    Boolean removeSubscription(String doctor_id, String patient_id);

    @Select("select count(*) from subscription where doctor_id=#{doctor_id} and patient_id=#{patient_id}")
    Integer Subscribed(String doctor_id,String patient_id);

    @Select("select count(*) from subscription where doctor_id=#{doctorId}")
    Integer FollowerNum(String doctorId);

    @Select("select count(*) from subscription where patient_id=#{userId}")
    Integer FolloweeNum(String userId);
}
