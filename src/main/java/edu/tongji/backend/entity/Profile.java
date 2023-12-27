package edu.tongji.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    private int patientId;

    private Boolean gender;

    private Object type;

    private Integer age;

    private String familyHistory;

    private Object diagnosedYear;
    private String anamnesis;

    private Object medicationPattern;

    private String allergy;

    private String medicationHistory;
    private Byte dietaryTherapy;
    private Byte exerciseTherapy;
    private Byte oralTherapy;
    private Byte insulinTherapy;
}
