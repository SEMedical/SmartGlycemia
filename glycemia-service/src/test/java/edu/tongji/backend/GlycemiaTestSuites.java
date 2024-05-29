package edu.tongji.backend;

import edu.tongji.backend.controller.GlycemiaController;
import edu.tongji.backend.dto.*;
import edu.tongji.backend.service.impl.GlycemiaServiceImpl;
import edu.tongji.backend.util.RegexUtils;
import edu.tongji.backend.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@Slf4j
public class GlycemiaTestSuites {
    @Autowired
    GlycemiaController glycemiaController;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private GlycemiaServiceImpl glycemiaService;
    private MockMvc mockMvc;
    private MockHttpSession session;

    //在每个测试方法执行之前都初始化MockMvc对象
    @BeforeEach
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        this.session = new MockHttpSession();
    }
    @Test
    public void TestChartRenderingSuites(){
        Response<Chart> response = glycemiaController.LookupChart("105", "Realtime", "2024-01-02");
        if(response.getResponse().getData().size()>0) {
            Map<LocalDateTime, Double> maps = response.getResponse().getData().get(0);
            for (Map.Entry<LocalDateTime, Double> entry : maps.entrySet()) {
                assertEquals(entry.getKey().toLocalDate().equals(LocalDate.now()), true);
                break;
            }
        }
        response = glycemiaController.LookupChart("1", "History", "2024-01-01");
    }
    @Test
    public void GlycemiaPromptSuites(){
        Response<Tip> tipResponse = glycemiaController.GetRealtimeTips("1", 21);
        //assertEquals(tipResponse.getResponse().getColor(),MyColor.RED);
    }
    @Test
    public void WeeklyOrMonthlyDataSuites(){
        Response<CompositeChart> months = glycemiaController.LookupChartRecord("1", "month", "2023-12-27");
        assertEquals(months.getResponse().getData().size()>0,true);
        Map<LocalDate, StatisticsCondensed> map = months.getResponse().getData().get(0);
        for (Map.Entry<LocalDate, StatisticsCondensed> entry : map.entrySet()) {
            assertEquals(RegexUtils.isDateInvalid(entry.getKey().toString()),false);
            assertEquals(entry.getKey().getMonth().toString().equals("DECEMBER"),true);
        }
        Response<CompositeChart> weeks = glycemiaController.LookupChartRecord("1", "week", "2023-12-27");
        assertEquals(weeks.getResponse().getData().size()>0,true);
        map = months.getResponse().getData().get(0);
        for (Map.Entry<LocalDate, StatisticsCondensed> entry : map.entrySet()) {
            assertEquals(RegexUtils.isDateInvalid(entry.getKey().toString()),false);
            Period period = Period.between(entry.getKey(), LocalDate.of(2023,12,7));
            assertEquals(period.getDays()<7,true);
        }
    }
}
