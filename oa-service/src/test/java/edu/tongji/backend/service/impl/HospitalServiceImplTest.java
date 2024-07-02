package edu.tongji.backend.service.impl;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 Victor Hu,rmEleven
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */





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
