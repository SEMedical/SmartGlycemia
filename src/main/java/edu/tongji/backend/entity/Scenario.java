package edu.tongji.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Scenario {
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
    @Column(name = "category", nullable = true, length = 30)
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    @Basic
    @Column(name = "calories", nullable = true)
    private Integer calories;



    @Basic
    @Column(name = "duration", nullable = true)
    private Integer duration;

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
