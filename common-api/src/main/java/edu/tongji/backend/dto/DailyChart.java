package edu.tongji.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyChart {
    List<Map<LocalDateTime,Double>> entry;
    Double highSta;//高血糖范围
    Double normalSta;//低血糖范围
    Double lowSta;//正常血糖范围
    @Override
    public String toString() {
        String res="";
        for(Map<LocalDateTime,Double> map: entry){
            for(Map.Entry<LocalDateTime,Double> entry:map.entrySet()){
                res+="At "+entry.getKey().toString()+" the value of glycemia is"+entry.getValue().toString()+"\n";
            }
        }
        res+="The percentage of hyperglycemia is "+ highSta.toString()+"\n";
        res+="The percentage of hypoglycemia is "+ normalSta.toString()+"\n";
        res+="The percentage of euGlycemia is "+ lowSta.toString()+"\n";
        return res;
    }
}
