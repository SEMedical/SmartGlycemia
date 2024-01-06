package edu.tongji.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SportPlanDTO {
    private int total_time;
    private int total_calorie;
    private String sport_type;
    private int recommend_time;
    private int recommend_calorie;
    private Boolean is_finished;
}
