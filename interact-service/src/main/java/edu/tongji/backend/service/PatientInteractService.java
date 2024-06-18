package edu.tongji.backend.service;

import edu.tongji.backend.dto.DoctorDTO2;

import java.util.List;

public interface PatientInteractService
{
    //查找医生
    List<DoctorDTO2> searchAll(String keyword);


    void subscribeDoctor(int userId,int doctorId);

    void appointDoctor(String department, String datetime, int hospitalId);

    Boolean unsubscribeDoctor(Integer user_id, int doctorId);
}
