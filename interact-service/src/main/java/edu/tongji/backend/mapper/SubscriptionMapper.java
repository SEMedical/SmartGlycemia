package edu.tongji.backend.mapper;

import edu.tongji.backend.dto.SinglePatientInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SubscriptionMapper {
    @Insert("insert into subscription(doctor_id,patient_id)" +
            " values(#{doctor_id},#{patient_id})")
    Boolean addSubscription(String doctor_id, String patient_id);

}
