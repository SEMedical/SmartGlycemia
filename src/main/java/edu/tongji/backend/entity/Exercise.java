package edu.tongji.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exercise {
    @Basic
    @Column(name = "patient_id", nullable = true)
    private Integer patientId;

    @Basic
    @Column(name = "start_time", nullable = true)
    private Timestamp startTime;

    @Basic
    @Column(name = "duration", nullable = true)
    private Integer duration;

    @Basic
    @Column(name = "category", nullable = true, length = 45)
    private String category;
    @Basic
    @Column(name = "frequency", nullable = true, length = 45)
    private String frequency;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "exercise_id", nullable = false)
    private int exerciseId;
}
