package edu.tongji.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "patient_id", nullable = false)
    private int patientId;

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    @Basic
    @Column(name = "gender", nullable = true)
    private Object gender;

    public Object getGender() {
        return gender;
    }

    public void setGender(Object gender) {
        this.gender = gender;
    }

    @Basic
    @Column(name = "type", nullable = true)
    private Object type;

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    @Basic
    @Column(name = "age", nullable = true)
    private Integer age;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Basic
    @Column(name = "family_history", nullable = true, length = -1)
    private String familyHistory;

    public String getFamilyHistory() {
        return familyHistory;
    }

    public void setFamilyHistory(String familyHistory) {
        this.familyHistory = familyHistory;
    }

    @Basic
    @Column(name = "diagnosed_year", nullable = true)
    private Object diagnosedYear;

    public Object getDiagnosedYear() {
        return diagnosedYear;
    }

    public void setDiagnosedYear(Object diagnosedYear) {
        this.diagnosedYear = diagnosedYear;
    }

    @Basic
    @Column(name = "anamnesis", nullable = true, length = -1)
    private String anamnesis;

    public String getAnamnesis() {
        return anamnesis;
    }

    public void setAnamnesis(String anamnesis) {
        this.anamnesis = anamnesis;
    }

    @Basic
    @Column(name = "medication_pattern", nullable = true)
    private Object medicationPattern;

    public Object getMedicationPattern() {
        return medicationPattern;
    }

    public void setMedicationPattern(Object medicationPattern) {
        this.medicationPattern = medicationPattern;
    }

    @Basic
    @Column(name = "allergy", nullable = true, length = -1)
    private String allergy;

    public String getAllergy() {
        return allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }

    @Basic
    @Column(name = "medication_history", nullable = true, length = -1)
    private String medicationHistory;

    public String getMedicationHistory() {
        return medicationHistory;
    }

    public void setMedicationHistory(String medicationHistory) {
        this.medicationHistory = medicationHistory;
    }

    @Basic
    @Column(name = "dietary_therapy", nullable = true)
    private Byte dietaryTherapy;

    public Byte getDietaryTherapy() {
        return dietaryTherapy;
    }

    public void setDietaryTherapy(Byte dietaryTherapy) {
        this.dietaryTherapy = dietaryTherapy;
    }

    @Basic
    @Column(name = "exercise_therapy", nullable = true)
    private Byte exerciseTherapy;

    public Byte getExerciseTherapy() {
        return exerciseTherapy;
    }

    public void setExerciseTherapy(Byte exerciseTherapy) {
        this.exerciseTherapy = exerciseTherapy;
    }

    @Basic
    @Column(name = "oral_therapy", nullable = true)
    private Byte oralTherapy;

    public Byte getOralTherapy() {
        return oralTherapy;
    }

    public void setOralTherapy(Byte oralTherapy) {
        this.oralTherapy = oralTherapy;
    }

    @Basic
    @Column(name = "insulin_therapy", nullable = true)
    private Byte insulinTherapy;

    public Byte getInsulinTherapy() {
        return insulinTherapy;
    }

    public void setInsulinTherapy(Byte insulinTherapy) {
        this.insulinTherapy = insulinTherapy;
    }
}
