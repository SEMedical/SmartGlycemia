package edu.tongji.backend.dto;

public class PatientList {
    String patientId;
    String patientName;
    String patientAvatar = "none";

    // 构造方法
    public PatientList(String patientId, String patientName) {
        this.patientId = patientId;
        this.patientName = patientName;
    }

    // 可选：添加getter和setter方法
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientAvatar() {
        return patientAvatar;
    }

    public void setPatientAvatar(String patientAvatar) {
        this.patientAvatar = patientAvatar;
    }
}