package edu.tongji.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.dto.ProfileDTO;
import edu.tongji.backend.entity.Profile;

import java.text.ParseException;

public interface IProfileService extends IService<Profile> {
    //Profile getCondensedProfile(String user_id);
    Profile getByPatientId(String patient_id);

    ProfileDTO getCompleteProfile(Integer patientId) throws ParseException;

    boolean updateProfile(Integer patientId, ProfileDTO profileDTO) throws ParseException;
    String getUserName(Integer patientId);
    String getContact2(Integer patientId);
    Integer getUserAge(Integer patientId);
}
