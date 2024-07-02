package edu.tongji.backend.mapper;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 LEAVE-cshj,Dawson128
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





import edu.tongji.backend.dto.DoctorDTO3;
import edu.tongji.backend.dto.PatientList;

import edu.tongji.backend.dto.SinglePatientInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DoctorInteractMapper {
    @Select("select patient_id,name as patientName from user u RIGHT JOIN profile p  ON  u.user_id = p.patient_id")
    PatientList[] getPatientList();
    @Select("select doctor_id from doctor;")
    List<String> getDoctorList();
    @Select("select id_card from doctor;")
    List<String> getIDList();
    @Select("select p.patient_id,name as patientName,created_at as timestamp from user u RIGHT JOIN profile p  ON  u.user_id = p.patient_id RIGHT JOIN subscription s ON s.patient_id=p.patient_id where doctor_id=#{doctor_id}")
    List<PatientList> getFollowerList(String doctor_id);
    @Select("select gender,type,age,p.height AS height,weight,diagnosed_year from user u RIGHT JOIN profile p  ON  u.user_id = p.patient_id where patient_id=#{patientId}")
    SinglePatientInfo getSinglePatientInfo(String patientId);
    @Select("select patient_id,name as patientName,age as PatientAge from user u RIGHT JOIN profile p  ON  u.user_id = p.patient_id where u.user_id=#{user_id} limit 1")
    PatientList getPatientInfo(String user_id);
    DoctorDTO3 getVerboseDoctorInfo(String doctor_id);
}
