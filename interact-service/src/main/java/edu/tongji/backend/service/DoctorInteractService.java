package edu.tongji.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.tongji.backend.dto.DoctorDTO3;
import edu.tongji.backend.dto.PatientList;
import edu.tongji.backend.dto.SinglePatientInfo;
import edu.tongji.backend.dto.applyList;

import java.util.List;

public interface DoctorInteractService {
//    获取患者列表
    PatientList[] getPatientList() throws JsonProcessingException;
//    医生获取单个患者信息
    SinglePatientInfo getSinglePatientInfo(String patientId);
//   医生确认患者申请
    Boolean confirmPatient(String messageId, String doctor_id) throws NullPointerException,IllegalArgumentException;

    //   医生获取申请列表
    applyList[] doctorGetApplicationList(String doctorId);

    Integer getFollowersNum(String doctor_id);

    Integer getFolloweesNum(String userId);

    List<PatientList> getFollowerList(String doctor_id) throws JsonProcessingException;

    Boolean discardPatient(String messageId, String userId);

    DoctorDTO3 getDoctorInfo(String doctor_id);
}
