package edu.tongji.backend.service;

import edu.tongji.backend.entity.Hospital;

public interface IHospitalService {
    public Integer addHospital(Hospital hospital);
    public void deleteHospital(int hospitalId);
}
