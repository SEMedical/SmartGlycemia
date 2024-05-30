package edu.tongji.backend.mapper;

import edu.tongji.backend.dto.DoctorDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PatientInteractMapper {
    //已被包含在下面的方法中
    List<DoctorDTO> searchName(String keyword);

    //查询医生

    List<DoctorDTO> searchAll(String keyword);

    //按科室预约医生
    //void appointDoctor(String department, String datetime, int hospitalId);
}
