package edu.tongji.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
/*
* 用于存储血糖统计信息，包括最大值，最小值，平均值，高血糖，低血糖，正常血糖的百分比
 */
public class Statistics {
    LocalDate time;
    Double minValue;
    Double maxValue;
    Double averageValue;//血糖平均值
    Double hyperglycemiaPercentage;//高血糖范围
    Double hypoglycemiaPercentage;//低血糖范围
    Double euGlycemiaPercentage;//正常血糖范围
}
