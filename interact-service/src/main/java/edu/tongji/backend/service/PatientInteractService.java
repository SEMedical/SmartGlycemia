package edu.tongji.backend.service;

import edu.tongji.backend.entity.Doctor;

import java.util.List;

public interface PatientInteractService
{
    //查找医生
    List<Doctor> searchAll(String keyword);


    void subscribeDoctor(int userId,int doctorId);

    void appointDoctor(String department, String datetime, int hospitalId);
}
