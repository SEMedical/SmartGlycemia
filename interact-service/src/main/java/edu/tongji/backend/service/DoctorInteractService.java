package edu.tongji.backend.service;

import edu.tongji.backend.dto.PatientList;
import edu.tongji.backend.dto.SinglePatientInfo;

public interface DoctorInteractService {
//    获取患者列表
    PatientList[] getPatientList();
//    医生获取单个患者信息
    SinglePatientInfo getSinglePatientInfo(String patientId);
}
