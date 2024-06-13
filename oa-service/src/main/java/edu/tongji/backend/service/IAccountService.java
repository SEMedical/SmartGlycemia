package edu.tongji.backend.service;

import edu.tongji.backend.dto.DoctorInfoDTO;
import edu.tongji.backend.entity.Doctor;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface IAccountService {
    public List<DoctorInfoDTO> getAccountList();
    public Integer addAccount(Doctor doctor, String contact, String address) throws NoSuchAlgorithmException;
    public void deleteAccount(int doctorId);

    Boolean repeatedIdCard(String idCard);

    Boolean updateAccount(Doctor doctor);
}
