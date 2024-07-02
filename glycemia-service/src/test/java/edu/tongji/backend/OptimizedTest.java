package edu.tongji.backend;

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
