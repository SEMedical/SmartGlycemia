package edu.tongji.backend;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 Victor Hu
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




import com.fasterxml.jackson.core.JsonProcessingException;
import edu.tongji.backend.dto.DoctorDTO2;
import edu.tongji.backend.dto.PatientList;
import edu.tongji.backend.service.DoctorInteractService;
import edu.tongji.backend.service.PatientInteractService;
import edu.tongji.backend.service.impl.DoctorInteractImpl;
import edu.tongji.backend.service.impl.DoctorInteractImpl;
import edu.tongji.backend.service.impl.PatientInteractServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class InteractSuiteTest {
    @Autowired
    PatientInteractServiceImpl patientInteractService;
    @Autowired
    DoctorInteractImpl doctorInteractService;
    @Test
    public void testKeyWordSearchBatch(){
        testKeyWordSearch("王");
        testKeyWordSearch("上海交通大学");
        testKeyWordSearch("黄浦");
        testKeyWordSearch("重庆");
        testKeyWordSearch("Dean");
        testKeyWordSearch("Ear");
        testKeyWordSearch("瑞金");
    }
    @Test
    public void getDoctorInfoBatch(){
        doctorInteractService.getDoctorInfo("108");
    }
    @Test
    public void getPatientList() throws JsonProcessingException {
        doctorInteractService.getPatientList();
    }
    @Test
    public void getPatientInfoBatch(){
        doctorInteractService.getSinglePatientInfo("8");
    }
    @Test
    public void testGetFollowerList() throws JsonProcessingException {
        List<PatientList> followerList = doctorInteractService.getFollowerList("121");
        System.out.println(followerList);
    }
    void testKeyWordSearch(String keyword){
        List<DoctorDTO2> doctors = patientInteractService.searchAll(keyword);
        for (DoctorDTO2 doctor : doctors) {
            Boolean flag1=doctor.getAddress().contains(keyword);
            Boolean flag2=doctor.getDepartment().contains(keyword);
            Boolean flag3=doctor.getContact().contains(keyword);
            Boolean flag4=doctor.getHospital_name().contains(keyword);
            Boolean flag5=doctor.getName().contains(keyword);
            Boolean flag6=doctor.getTitle().contains(keyword);
            Boolean flag7=doctor.getIntroduction().contains(keyword);
            assertEquals(flag1||flag3||flag6||flag7||flag4||flag2||flag5,true);
        }
    }
}
