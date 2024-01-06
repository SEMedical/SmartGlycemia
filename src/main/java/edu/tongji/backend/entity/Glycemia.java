package edu.tongji.backend.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Glycemia {
    @TableId
    private int patientId;

    private Double glycemia;

    private Timestamp recordTime;
}
