package edu.tongji.backend.clients;

import edu.tongji.backend.config.FeignConfig;
import edu.tongji.backend.dto.Intervals;
import edu.tongji.backend.entity.Profile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@FeignClient(name="exercise-service",configuration = FeignConfig.class)
public interface ExerciseClient {
    @GetMapping(value = "/api/sports/getExerciseIntvl")
    Intervals getExerciseIntervals(@RequestParam("category") String category,@RequestParam("date") String date);
}