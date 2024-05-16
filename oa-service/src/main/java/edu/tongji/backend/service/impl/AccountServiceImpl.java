package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.entity.Doctor;
import edu.tongji.backend.mapper.DoctorMapper;
import edu.tongji.backend.service.IAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AccountServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements IAccountService {
    @Autowired
    DoctorMapper doctorMapper;

    @Override
    public List<Doctor> getAccountList() {
        return doctorMapper.getAccountList();
    }

    @Override
    public void addAccount(Doctor doctor) {
        try {

            doctorMapper.insert(doctor);
        }catch (Exception e){
            e.printStackTrace();
        }
        return;
    }

    @Override
    public void deleteAccount(int doctorId) {
        doctorMapper.deleteById(doctorId);
        return;
    }
}
