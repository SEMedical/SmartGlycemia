package edu.tongji.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Questionnaire {
    private Integer patientId;
    private Integer questionnaireId;
    private String version;
    private Integer template;
    private String data;
}
