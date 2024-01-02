package edu.tongji.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.dto.ProfileDTO;
import edu.tongji.backend.entity.Profile;

public interface IProfileService extends IService<Profile> {
    //Profile getCondensedProfile(String user_id);
    Profile getByPatientId(String patient_id);

    ProfileDTO getCompleteProfile(Integer patient_id);
}
