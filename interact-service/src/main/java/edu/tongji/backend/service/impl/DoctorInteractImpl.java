package edu.tongji.backend.service.impl;

import edu.tongji.backend.dto.PatientList;
import edu.tongji.backend.dto.SinglePatientInfo;
import edu.tongji.backend.mapper.DoctorInteractMapper;
import edu.tongji.backend.service.DoctorInteractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoctorInteractImpl implements DoctorInteractService {
    @Autowired
    DoctorInteractMapper doctorInteractMapper;
//医生获取患者列表
    @Override
    public PatientList[] getPatientList() {
        PatientList[] p_list=doctorInteractMapper.getPatientList();
        return p_list;
    }

    @Override
    public SinglePatientInfo getSinglePatientInfo(String patientId) {
        SinglePatientInfo p_info=doctorInteractMapper.getSinglePatientInfo(patientId);
        return p_info;
    }


}
