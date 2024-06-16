package edu.tongji.backend.dto;

public class applyList {
    private String messageId;
    private String patientId;

    // No-argument constructor
    public applyList() {
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
