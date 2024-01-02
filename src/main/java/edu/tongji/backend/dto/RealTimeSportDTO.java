package edu.tongji.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RealTimeSportDTO {
    private int calorie;
    private double distance;//以公里为单位
    private double speed;
    private String time;
}
