package edu.tongji.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Scenario {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "patient_id", nullable = false)
    private int patientId;


    @Basic
    @Column(name = "category", length = 30)
    private String category;


    @Basic
    @Column(name = "duration")
    private Integer duration;


    @Basic
    @Column(name = "calories")
    private Integer calories;
}
