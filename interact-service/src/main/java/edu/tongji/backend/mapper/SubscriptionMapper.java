package edu.tongji.backend.mapper;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 Victor Hu
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */




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
