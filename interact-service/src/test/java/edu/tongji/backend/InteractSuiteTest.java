package edu.tongji.backend;

import edu.tongji.backend.dto.DoctorDTO2;
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
