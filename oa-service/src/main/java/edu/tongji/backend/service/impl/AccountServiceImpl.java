package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.clients.UserClient2;
import edu.tongji.backend.dto.DoctorInfoDTO;
import edu.tongji.backend.entity.Doctor;
import edu.tongji.backend.entity.Hospital;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.mapper.DoctorMapper;
import edu.tongji.backend.mapper.HospitalMapper;
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
    @Autowired
    HospitalMapper hospitalMapper;

    @Override
    public List<DoctorInfoDTO> getAccountList() {
        return doctorMapper.getAccountList();
    }

    @Override
    public void addAccount(Doctor doctor, String contact) {
        System.out.println("判断contact是否已经存在");

        if (userClient2.repeatedContact(contact)) {  // 判断contact是否已经存在
            throw new RuntimeException("repeated contact is not allowed");
        }

        System.out.println(doctor.getHospitalId());
        QueryWrapper<Hospital> wrapper = new QueryWrapper<>();
        wrapper.eq("hospital_id", doctor.getHospitalId());
        Hospital result = hospitalMapper.selectOne(wrapper);
        if (result == null) {  // hospital_id不存在
            throw new RuntimeException("hospital does not exist");
        }

//        验证数据有效性（id_card，department，title）
//        自动生成ID
//        加拦截器后：请求头保存管理员useId，身份是admin
//        错误处理：log4j 接口 sl4j 实现，低耦合
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
