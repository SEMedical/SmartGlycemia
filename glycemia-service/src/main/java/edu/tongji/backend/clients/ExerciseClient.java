package edu.tongji.backend.clients;

import edu.tongji.backend.dto.Intervals;
import edu.tongji.backend.entity.Profile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@FeignClient("exercise-service")
public interface ExerciseClient {
    @GetMapping("/api/sports/getExerciseIntvl")
    Intervals getExerciseIntervals(@RequestParam("category") String category,@RequestParam("date") String date, HttpServletRequest request);
}