package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.org.apache.xpath.internal.operations.Bool;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    //TODO:it's repeated,so it need to be removed later
    private String convertToSHA256(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] encodedhash = digest.digest(password.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedhash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    @Override
    public void addAccount(Doctor doctor, String contact,String address) throws NoSuchAlgorithmException {
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
                defaultPassword=convertToSHA256(idCard.substring(idCard.length() - 6));
        }

        User user = new User(doctorId, address, "Alice", contact, defaultPassword, "doctor");
        userClient2.addUser(user);
        doctorMapper.insert(doctor);
    }

    @Override
    public void deleteAccount(int doctorId) {
        try {
            doctorMapper.deleteById(doctorId);
            userClient2.rmUser(doctorId);
        }catch (Exception e){
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        return;
    }
}
