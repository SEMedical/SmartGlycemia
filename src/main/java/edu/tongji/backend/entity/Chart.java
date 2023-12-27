package edu.tongji.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chart {
    Integer error_code;
    List<Map<LocalDateTime,Double>> data;

    @Override
    public String toString() {
        String res="";
        if(error_code!=200)
            res+="Can't get the correct chart data, error code:"+error_code;
        else{
            for(Map<LocalDateTime,Double> map:data){
                for(Map.Entry<LocalDateTime,Double> entry:map.entrySet()){
                    res+="At "+entry.getKey().toString()+" the value of glycemia is"+entry.getValue().toString()+"\n";
                }
            }
        }
        return res;
    }
}
