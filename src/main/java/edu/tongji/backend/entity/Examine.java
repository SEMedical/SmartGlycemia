package edu.tongji.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Examine {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "examination_id", nullable = false)
    private int examinationId;

    @Basic
    @Column(name = "patient_id", nullable = true)
    private Integer patientId;

    @Basic
    @Column(name = "weight", nullable = true, precision = 0)
    private Integer weight;

    @Basic
    @Column(name = "health_state", nullable = true)
    private Object healthState;

    @Basic
    @Column(name = "high_blood_pressure", nullable = true)
    private Integer highBloodPressure;


    @Basic
    @Column(name = "sleep_quality", nullable = true)
    private Object sleepQuality;

    @Basic
    @Column(name = "examine_time", nullable = true)
    private Timestamp examineTime;

    @Basic
    @Column(name = "low_blood_pressure", nullable = true)
    private Integer lowBloodPressure;

    @Basic
    @Column(name = "hyperglycemia", nullable = true)
    private Object hyperglycemia;

    @Basic
    @Column(name = "trend", nullable = true)
    private Object trend;

    @Basic
    @Column(name = "exercise_dose", nullable = true)
    private Object exerciseDose;
}
