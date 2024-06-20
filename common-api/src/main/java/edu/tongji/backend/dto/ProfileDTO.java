package edu.tongji.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
    private String gender;
    private Integer age;
    private String height;

    public ProfileDTO(String gender, Integer age, String height, String weight, String diabetesType, String complications, Integer diagnosisYear, String familyHistory) {
        this.gender = gender;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.diabetesType = diabetesType;
        this.complications = complications;
        this.diagnosisYear = diagnosisYear;
        this.familyHistory = familyHistory;
    }

    private String weight;
    private String diabetesType;
    private String complications;
    private Integer diagnosisYear;
    private String familyHistory;
    private String name;
    private String contact;
}
