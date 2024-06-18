package edu.tongji.backend.service.impl;

import edu.tongji.backend.controller.AccountController;
import edu.tongji.backend.entity.Hospital;
import edu.tongji.backend.mapper.HospitalMapper;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(JUnit4ClassRunner.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class HospitalServiceImplTest {
    @Mock
    private HospitalMapper hospitalMapper;

    @InjectMocks
    private HospitalServiceImpl hospitalService;

    @Test
    void testAddHospitalSuccess() {
//        String hospitalName = "测试医院";
//        String level = "三甲";
//        String address = "嘉定区曹安公路";
//        BigDecimal latitude = new BigDecimal(31.0);
//        BigDecimal longitude = new BigDecimal(120.0);
//        String zipcode = "200064";
//        String hospitalPhone = "120120";
//        String outpatientHours = "8:00-17:00";
//        String introduction = "这是测试医院";
        Hospital hospital = new Hospital();
        when(hospitalMapper.getMaxId()).thenReturn(1);

        hospitalService.addHospital(hospital);

        assertEquals(2, hospital.getHospitalId());
        verify(hospitalMapper, times(1)).insert(hospital);
    }

    @Test
    void testAddHospitalException() {

        Hospital hospital = new Hospital();
        when(hospitalMapper.getMaxId()).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> hospitalService.addHospital(hospital));

        assertEquals("Database error", exception.getMessage());
        verify(hospitalMapper, times(0)).insert(hospital);
    }

    @Test
    void testDeleteHospitalSuccess() {
        when(hospitalMapper.deleteById(1)).thenReturn(1);

        assertDoesNotThrow(() -> hospitalService.deleteHospital(1));

        verify(hospitalMapper, times(1)).deleteById(1);
    }

    @Test
    void testDeleteHospitalNotFound() {
        when(hospitalMapper.deleteById(999)).thenReturn(0);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> hospitalService.deleteHospital(999));

        assertEquals("The Hospital 999 doesn't exist or has been removed earlier!", exception.getMessage());
        verify(hospitalMapper, times(1)).deleteById(999);
    }
}