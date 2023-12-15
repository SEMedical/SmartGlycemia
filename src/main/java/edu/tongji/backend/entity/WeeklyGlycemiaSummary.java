package edu.tongji.backend.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyGlycemiaSummary {
    @Basic
    @Column(name = "patient_id", nullable = false)
    private int patientId;
    @Basic
    @Column(name = "week_number", nullable = true)
    private Integer weekNumber;
    @Basic
    @Column(name = "day_of_week", nullable = true)
    private Integer dayOfWeek;
    @Basic
    @Column(name = "max_glycemia", nullable = true, precision = 1)
    private BigDecimal maxGlycemia;
    @Basic
    @Column(name = "min_glycemia", nullable = true, precision = 1)
    private BigDecimal minGlycemia;
}
