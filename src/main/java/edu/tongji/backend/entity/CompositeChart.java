package edu.tongji.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
//复合统计信息的图表
public class CompositeChart {
    Integer error_code;
    List<Map<LocalDate,Statistics>> data;

    @Override
    public String toString() {
        String res="";
        if(error_code!=200)
            res+="Can't get the correct chart data, error code:"+error_code;
        else{
            for(Map<LocalDate,Statistics> map:data){
                for(Map.Entry<LocalDate,Statistics> entry:map.entrySet()){
                    res+="At "+entry.getKey().toString()+" the statistics of glycemia is\n"
                            +entry.getValue().toString()+"\n";
                }
            }
        }
        return res;
    }
}
