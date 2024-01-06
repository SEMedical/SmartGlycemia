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
    private String weight;
    private String diabetesType;
    private String complications;
    private Integer diagnosisYear;
    private String familyHistory;
}
