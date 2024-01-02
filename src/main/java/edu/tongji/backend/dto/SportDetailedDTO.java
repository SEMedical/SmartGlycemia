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
public SportDetailedDTO(int[] minute_record, int[] calorie_record, String mean_speed, String sum_duration, double sum_distance) {
    this.minute_record = new int[minute_record.length];
    for(int i=0;i<minute_record.length;i++)
        this.minute_record[i]=minute_record[i];
    this.calorie_record = new int[calorie_record.length];
    for(int i=0;i<calorie_record.length;i++)
        this.calorie_record[i]=calorie_record[i];
    this.mean_speed = mean_speed;
    this.sum_duration = sum_duration;
    this.sum_distance = sum_distance;
}
}