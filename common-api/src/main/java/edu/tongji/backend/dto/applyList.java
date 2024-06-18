package edu.tongji.backend.dto;

public class applyList {
    private String messageId;
    private String patientId;
    private String patientName;
    private String patientAge;

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(String patientAge) {
        this.patientAge = patientAge;
    }

    // No-argument constructor
    public applyList() {
    }

    public applyList(String messageId, String patientId, String patientName, String patientAge) {
        this.messageId = messageId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientAge = patientAge;
    }

    // All-argument constructor
    public applyList(String messageId, String patientId) {
        this.messageId = messageId;
        this.patientId = patientId;
    }

    // Getters and Setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }


}
