package edu.tongji.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

@Data
@NoArgsConstructor
public class SportDetailedDTO {
    private int[] minute_record;
    private int[] calorie_record;
    private String mean_speed;
    private String sum_duration;
    private double sum_distance;

}