package edu.tongji.backend.service;

import edu.tongji.backend.dto.DoctorInfoDTO;
import edu.tongji.backend.entity.Doctor;

import java.util.List;

public interface IAccountService {
    public List<DoctorInfoDTO> getAccountList();
    public void addAccount(Doctor doctor, String contact);
    public void deleteAccount(int doctorId);
}
