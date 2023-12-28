package edu.tongji.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exercise {
    private Integer patientId;

    private String startTime;

    private Integer duration;//unit:minute

    private String category;

    private String frequency;

    private int exerciseId;
}
