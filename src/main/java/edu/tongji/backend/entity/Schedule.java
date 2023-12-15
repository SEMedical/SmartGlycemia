package edu.tongji.backend.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {
    @Basic
    @Column(name = "patient_id", nullable = false)
    private int patientId;
    @Basic
    @Column(name = "exercise_date", nullable = true)
    private Date exerciseDate;
    @Basic
    @Column(name = "category", nullable = true, length = 30)
    private String category;
    @Basic
    @Column(name = "intensity", nullable = true)
    private Object intensity;
    @Basic
    @Column(name = "timing", nullable = true)
    private Time timing;
}
