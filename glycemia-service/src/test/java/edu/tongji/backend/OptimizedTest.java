package edu.tongji.backend;

import edu.tongji.backend.controller.GlycemiaController;
import edu.tongji.backend.dto.DailyChart;
import edu.tongji.backend.service.impl.GlycemiaServiceImpl;
import edu.tongji.backend.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

@Slf4j
@SpringBootTest
public class OptimizedTest {
    @Autowired
    private GlycemiaServiceImpl glycemiaService;
    @Autowired
    private GlycemiaController glycemiaController;
    @Test
    void TestGlycemiaChartsBatch() throws Exception {
       //glycemiaService.showGlycemiaDiagram("History","1",LocalDate.of(2024,1,1));
        //ResponseEntity<Response<DailyChart>> responseResponseEntity = glycemiaController.GetDailyChart("1", "2024-06-02");
        glycemiaService.showDailyGlycemiaDiagram("13",LocalDate.of(2024,6,25));
       //glycemiaService.showGlycemiaHistoryDiagram("Month","1",LocalDate.of(2024,1,1));
    }
}
