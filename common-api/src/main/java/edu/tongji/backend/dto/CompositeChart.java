package edu.tongji.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
//复合统计信息的图表
public class CompositeChart {
    List<Map<LocalDate, StatisticsCondensed>> data;
    Double hyperglycemiaPercentage;//高血糖范围
    Double hypoglycemiaPercentage;//低血糖范围
    Double euGlycemiaPercentage;//正常血糖范围
    @Override
    public String toString() {
        String res="";
        for(Map<LocalDate,StatisticsCondensed> map:data) {
            for (Map.Entry<LocalDate, StatisticsCondensed> entry : map.entrySet()) {
                res += "At " + entry.getKey().toString() + " the statistics of glycemia is\n"
                        + entry.getValue().toString() + "\n";
            }
        }
        res+="The percentage of hyperglycemia is "+hyperglycemiaPercentage.toString()+"\n";
        res+="The percentage of hypoglycemia is "+hypoglycemiaPercentage.toString()+"\n";
        res+="The percentage of euGlycemia is "+euGlycemiaPercentage.toString()+"\n";
        return res;
    }
}
