package edu.tongji.backend.service;

import edu.tongji.backend.entity.Doctor;

import java.util.List;

public interface IAccountService {
    public List<Doctor> getAccountList();
    public void addAccount(Doctor doctor);
    public void deleteAccount(int doctorId);
}
