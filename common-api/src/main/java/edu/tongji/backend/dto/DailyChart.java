package edu.tongji.backend.dto;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 Victor Hu
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */




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
    Double highSta=0.0;//高血糖范围
    Double normalSta=0.0;//低血糖范围
    Double lowSta=0.0;//正常血糖范围
    @Override
    public String toString() {
        String res="";
        for(Map<LocalDateTime,Double> map: entry){
            for(Map.Entry<LocalDateTime,Double> entry:map.entrySet()){
                res+="At "+entry.getKey().toString()+" the value of glycemia is"+entry.getValue().toString()+"\n";
            }
        }
        res+="The percentage of hyperglycemia is "+String.valueOf(highSta)+"\n";
        res+="The percentage of hypoglycemia is "+String.valueOf(normalSta)+"\n";
        res+="The percentage of euGlycemia is "+ String.valueOf(lowSta)+"\n";
        return res;
    }
}
