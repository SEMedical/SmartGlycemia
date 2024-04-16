package edu.tongji.backend;

import edu.tongji.backend.controller.GlycemiaController;
import edu.tongji.backend.mapper.GlycemiaMapper;
import edu.tongji.backend.service.impl.GlycemiaServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GlycemiaApplicationTests {
    @Autowired
    GlycemiaMapper glycemiaMapper;
    @Autowired
    GlycemiaController glycemiaController;
    @Autowired
    GlycemiaServiceImpl glycemiaService;
    //一个用于测试实时获取运动数据的测试用例
    @Test
    void showGlycemiaHistoryDiagramBatch() {//Only w/ Redis:11s891(66.8%)  w/Redis&Bloom 4s103(65.5%,88.5%) None:35s844
        glycemiaService.Init_GlycemiaHistoryDiagram();
        for(int i=0;i<100;i++)
            glycemiaService.showGlycemiaHistoryDiagram("Week", "2", LocalDate.of(2023, 12, 27));
    }
    @Test
    void showGlycemiaDiagram() {//Only w/ Redis:8s964(-115.7%?)  w/Redis&Bloom 1s367(84.8%,67.1%) None:4s155
        glycemiaService.Init_GlycemiaDiagram();
        for(int i=0;i<5;i++)
            glycemiaService.showGlycemiaDiagram("History", "2", LocalDate.of(2023, 12, 28));
    }
    @Test
    void showDailyGlycemiaDiagram() {//Only w/ Redis:9s842(-115.7%?)  w/Redis&Bloom 2s338(84.8%,67.1%) None:5s425
        glycemiaService.Init_DailyGlycemiaDiagram();
        for(int i=0;i<5;i++)
            glycemiaService.showDailyGlycemiaDiagram( "1", LocalDate.of(2023, 12, 27));
    }
    @Test
    void getLatestGlycemia(){
        //assertThrows(GlycemiaException.class, () -> {
        //    glycemiaService.getLatestGlycemia("1");
        //});
    }
    @Test
    void testSelectGlycemia(){
        System.out.println("Start test");

        System.out.println("End test");
//        assertThrows(GlycemiaException.class, () -> {
//            glycemiaController.LookupChart("key","History", "2", "2023-12-27");
//
//        });
    }
    @Test
    void testSelectGlycemiaRecord(){
        System.out.println("Start test");
//        assertThrows(GlycemiaException.class, () -> {
//            //glycemiaController.LookupChartRecord("Week", "2", "2023-12-27");
//        });
        System.out.println("End test");

    }
    @Test
    void testDailyDiagram(){
        System.out.println(glycemiaService.showDailyGlycemiaDiagram("1", LocalDate.of(2024, 1, 2)));
    }
}
