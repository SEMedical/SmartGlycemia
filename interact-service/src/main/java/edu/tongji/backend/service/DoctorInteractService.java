package edu.tongji.backend.service;

import edu.tongji.backend.dto.PatientList;
import edu.tongji.backend.dto.SinglePatientInfo;
import edu.tongji.backend.dto.applyList;

public interface DoctorInteractService {
//    获取患者列表
    PatientList[] getPatientList();
//    医生获取单个患者信息
    SinglePatientInfo getSinglePatientInfo(String patientId);
//   医生确认患者申请
    Boolean confirmPatient(String messageId, String doctor_id) throws NullPointerException;

    //   医生获取申请列表
    applyList[] doctorGetApplicationList(String doctorId);
}
