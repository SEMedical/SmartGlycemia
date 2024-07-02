package edu.tongji.backend.mapper;

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
