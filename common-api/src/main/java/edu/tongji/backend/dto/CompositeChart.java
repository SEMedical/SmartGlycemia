package edu.tongji.backend.dto;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 All contributors of the project
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
