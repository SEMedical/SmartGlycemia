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
    @Column(name = "start_day", nullable = true)
    private Date startDay;

    public Date getStartDay() {
        return startDay;
    }

    public void setStartDay(Date startDay) {
        this.startDay = startDay;
    }

    @Basic
    @Column(name = "end_day", nullable = true)
    private Date endDay;

    public Date getEndDay() {
        return endDay;
    }

    public void setEndDay(Date endDay) {
        this.endDay = endDay;
    }

    @Basic
    @Column(name = "frequency", nullable = true)
    private Integer frequency;

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
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
    @Column(name = "intensity", nullable = true)
    private Object intensity;

    public Object getIntensity() {
        return intensity;
    }

    public void setIntensity(Object intensity) {
        this.intensity = intensity;
    }

    @Basic
    @Column(name = "timing", nullable = true)
    private Time timing;

    public Time getTiming() {
        return timing;
    }

    public void setTiming(Time timing) {
        this.timing = timing;
    }

    @Basic
    @Column(name = "duration", nullable = true)
    private Integer duration;

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    private int calories;
}
