package edu.tongji.backend.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyGlycemiaSummary {
    @Basic
    @Column(name = "patient_id", nullable = false)
    private int patientId;
    @Basic
    @Column(name = "record_date", nullable = true)
    private Date recordDate;
    @Basic
    @Column(name = "max_glycemia", nullable = true, precision = 1)
    private BigDecimal maxGlycemia;
    @Basic
    @Column(name = "min_glycemia", nullable = true, precision = 1)
    private BigDecimal minGlycemia;


}
