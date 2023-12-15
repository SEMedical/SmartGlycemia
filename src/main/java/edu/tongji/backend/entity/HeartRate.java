package edu.tongji.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeartRate {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "exercise_id", nullable = false)
    private int exerciseId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "interval_seq", nullable = false)
    private int intervalSeq;

    @Basic
    @Column(name = "avg_interval_heart_rate", nullable = true)
    private Integer avgIntervalHeartRate;
}
