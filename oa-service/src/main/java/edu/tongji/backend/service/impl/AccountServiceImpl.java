package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.clients.UserClient2;
import edu.tongji.backend.dto.DoctorInfoDTO;
import edu.tongji.backend.entity.Doctor;
import edu.tongji.backend.entity.User;
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
    @Autowired
    UserClient2 userClient2;

    @Override
    public List<DoctorInfoDTO> getAccountList() {
        return doctorMapper.getAccountList();
    }

    @Override
    public void addAccount(Doctor doctor) {
        int doctorId = doctor.getDoctorId();
        String idCard = doctor.getIdCard();
        String defaultPassword = "";
        if (idCard != null) {
            defaultPassword = idCard;
            if (idCard.length() >= 6)
                defaultPassword = idCard.substring(idCard.length() - 6);
        }

        User user = new User(doctorId, "4800,Caoan Hwy,Jiading,Shanghai", "Alice", "02165980001", defaultPassword, "doctor");
        userClient2.addUser(user);
        doctorMapper.insert(doctor);
        return;
    }

    @Override
    public void deleteAccount(int doctorId) {
        doctorMapper.deleteById(doctorId);
        userClient2.rmUser(doctorId);
        return;
    }
}
