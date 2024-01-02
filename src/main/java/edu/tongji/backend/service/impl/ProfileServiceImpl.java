package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.dto.ProfileDTO;
import edu.tongji.backend.entity.Profile;
import edu.tongji.backend.mapper.ComplicationMapper;
import edu.tongji.backend.mapper.ProfileMapper;
import edu.tongji.backend.service.IProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ProfileServiceImpl extends ServiceImpl<ProfileMapper, Profile> implements IProfileService {
    @Autowired
    ProfileMapper profileMapper;
    //TODO: shorten the profile

    @Autowired
    ComplicationMapper complicationMapper;

    @Override
    public Profile getByPatientId(String patient_id) {
        return profileMapper.getByPatientIdProfile(Integer.valueOf(patient_id));
    }

    @Override
    public ProfileDTO getCompleteProfile(Integer patient_id) {
        ProfileDTO profileDTO = new ProfileDTO();

        Profile profile = profileMapper.getByPatientIdProfile(patient_id);
        profileDTO.setGender(profile.getGender());
        profileDTO.setAge(profile.getAge());
        profileDTO.setWeight(profile.getWeight());
        profileDTO.setHeight(profile.getHeight());
        if (Objects.equals(profile.getType(), "I")) {
            profileDTO.setDiabetesType("I型糖尿病");
        } else if (Objects.equals(profile.getType(), "II")) {
            profileDTO.setDiabetesType("II型糖尿病");
        } else if (Objects.equals(profile.getType(), "gestational")) {
            profileDTO.setDiabetesType("妊娠期糖尿病");
        } else {
            profileDTO.setDiabetesType("");
        }
        profileDTO.setDiagnosisYear(profile.getDiagnosedYear() != null ? Integer.valueOf(profile.getDiagnosedYear()) : null);
        profileDTO.setFamilyHistory(profile.getFamilyHistory());

        List<String> complications = complicationMapper.getByPatientId(patient_id);
        StringBuilder complicationStr = getComplicationStr(complications);
        if (!complicationStr.isEmpty()) {
            complicationStr.deleteCharAt(complicationStr.length() - 1);
        }
        profileDTO.setComplications(complicationStr.toString());

        return profileDTO;
    }

    private static StringBuilder getComplicationStr(List<String> complications) {
        StringBuilder complicationStr = new StringBuilder();
        for (String complication : complications) {
            switch (complication) {
// 'diabetic foot', 'diabetic eye', 'diabetic kidney', 'diabetic cardiovascular disease', ' diabetic neuropathy', 'diabetic skin disease', 'hypertension', 'hyperlipidemia', 'others'
                case "diabetic foot":
                    complicationStr.append("糖尿病足、");
                    break;
                case "diabetic eye":
                    complicationStr.append("糖尿病眼、");
                    break;
                case "diabetic kidney":
                    complicationStr.append("糖尿病肾、");
                    break;
                case "diabetic cardiovascular disease":
                    complicationStr.append("糖尿病心血管疾病、");
                    break;
                case "diabetic neuropathy":
                    complicationStr.append("糖尿病神经病变、");
                    break;
                case "diabetic skin disease":
                    complicationStr.append("糖尿病皮肤病、");
                    break;
                case "hypertension":
                    complicationStr.append("高血压、");
                    break;
                case "hyperlipidemia":
                    complicationStr.append("高血脂、");
                    break;
                case "others":
                    complicationStr.append("其他、");
                    break;
            }
        }
        return complicationStr;
    }
}
