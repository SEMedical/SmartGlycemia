package edu.tongji.backend.service.impl;

import edu.tongji.backend.entity.Hospital;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HospitalServiceImplTest {
    @Autowired
    private HospitalServiceImpl hospitalService;

    @Test
    void addHospital() {
        int hospitalId = 5;
        String hospitalName = "测试医院";
        String level = "三甲";
        String address = "嘉定区曹安公路";
        BigDecimal latitude = new BigDecimal(31.0);
        BigDecimal longitude = new BigDecimal(120.0);
        String zipcode = "200064";
        String hospitalPhone = "120120";
        String outpatientHours = "8:00-17:00";
        String introduction = "这是测试医院";
        Hospital hospital = new Hospital(hospitalId, hospitalName, level, address, latitude, longitude,
                                         zipcode, hospitalPhone, outpatientHours, introduction);
        hospitalService.addHospital(hospital);
    }

    @Test
    void deleteHospital() {
    }
}