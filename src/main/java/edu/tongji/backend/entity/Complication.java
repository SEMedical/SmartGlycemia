package edu.tongji.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Complication {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "patient_id", nullable = false)
    private int patientId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "symptom", nullable = false)
    private Object symptom;
}
