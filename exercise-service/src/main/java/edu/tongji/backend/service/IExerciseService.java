package edu.tongji.backend.service;

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




import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.dto.RealTimeSportDTO;
import edu.tongji.backend.dto.SportDetailedDTO;
import edu.tongji.backend.dto.SportPlanDTO;
import edu.tongji.backend.dto.SportRecordDTO;
import edu.tongji.backend.entity.Exercise;
import edu.tongji.backend.dto.Intervals;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import java.util.List;

public interface IExerciseService extends IService<Exercise> {
    Intervals getExerciseIntervalsInOneDay(String category,String userId, String date);
    Integer addExercise(String userId,Double longitude,Double latitude,HttpServletRequest request);
    Integer finishExercise(String userId,String token);
    SportRecordDTO getSportRecord(String userId);
    SportDetailedDTO getDetailedSportRecord(String userId,int time_type,String category);
    Integer getRealTimeHeartRate(String userId);
    SportPlanDTO getSportPlan(String userId);
    RealTimeSportDTO getRealTimeSport(String userId,Double longitude,Double latitude) throws Exception;
}
