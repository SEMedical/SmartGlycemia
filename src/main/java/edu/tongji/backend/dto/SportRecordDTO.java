package edu.tongji.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SportRecordDTO {
    private int total_minute;
    private int total_calorie;
    private int[] minute_record=new int[8];//最近七天，每天的运动时间
    private HashMap<String, CategoryRecordDTO> sport_records;
}
