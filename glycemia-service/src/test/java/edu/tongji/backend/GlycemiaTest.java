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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@Slf4j
@SpringBootTest
public class GlycemiaTest {
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
        ResponseEntity<Response<Chart>> response = glycemiaController.LookupChart("1", "Realtime", "2024-01-02");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        if(response.getBody().getResponse().getData().size()>0) {
            Map<LocalDateTime, Double> maps = response.getBody().getResponse().getData().get(0);
            for (Map.Entry<LocalDateTime, Double> entry : maps.entrySet()) {
                assertEquals(entry.getKey().toLocalDate().equals(LocalDate.now()), true);
                break;
            }
        }
        response = glycemiaController.LookupChart("1", "History", "2024-01-01");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        response = glycemiaController.LookupChart("1", "History", "3024-01-01");
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        response = glycemiaController.LookupChart("1", "History", "1024-01-01");
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        response = glycemiaController.LookupChart("1", "History", "2024.01.01");
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        response = glycemiaController.LookupChart("1", "History", null);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        response = glycemiaController.LookupChart("1", "Mess", null);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        response = glycemiaController.LookupChart("1", "Realtime", "2024-01-01");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        response = glycemiaController.LookupChart("1", "realtime", "2024-01-01");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void GlycemiaPromptSuites(){
        Response<Tip> tipResponse = glycemiaController.GetRealtimeTips("1", 21);
        tipResponse = glycemiaController.GetRealtimeTips("1", 5);
        tipResponse = glycemiaController.GetRealtimeTips("1", 70);
        tipResponse = glycemiaController.GetRealtimeTips("96", 21);
        tipResponse = glycemiaController.GetRealtimeTips("101", 21);
        tipResponse = glycemiaController.GetRealtimeTips("24", 70);
        //assertEquals(tipResponse.getResponse().getColor(),MyColor.RED);
    }
    @Test
    public void RealTimeGlycemiaSuites(){
        //Just enumerate
        ResponseEntity<Response<Double>> response = glycemiaController.GetRealtimeGlycemia("1");
        response = glycemiaController.GetRealtimeGlycemia("1");
        response = glycemiaController.GetRealtimeGlycemia("1");
        response = glycemiaController.GetRealtimeGlycemia("96");
        response = glycemiaController.GetRealtimeGlycemia("101");
        response = glycemiaController.GetRealtimeGlycemia("24");
        //assertEquals(tipResponse.getResponse().getColor(),MyColor.RED);
    }
    @Test
    public void WeeklyOrMonthlyDataSuites(){
        //#1
        ResponseEntity<Response<CompositeChart>> months = glycemiaController.LookupChartRecord("1", "month", "2023-12-27");
        assertEquals(months.getBody().getResponse().getData().size()>0,true);
        Map<LocalDate, StatisticsCondensed> map = months.getBody().getResponse().getData().get(0);
        for (Map.Entry<LocalDate, StatisticsCondensed> entry : map.entrySet()) {
            assertEquals(RegexUtils.isDateInvalid(entry.getKey().toString()),false);
            assertEquals(entry.getKey().getMonth().toString().equals("DECEMBER"),true);
        }
        //#2
        ResponseEntity<Response<CompositeChart>> weeks = glycemiaController.LookupChartRecord("1", "week", "2023-12-27");
        assertEquals(weeks.getBody().getResponse().getData().size()>0,true);
        map = months.getBody().getResponse().getData().get(0);
        for (Map.Entry<LocalDate, StatisticsCondensed> entry : map.entrySet()) {
            assertEquals(RegexUtils.isDateInvalid(entry.getKey().toString()),false);
            Period period = Period.between(entry.getKey(), LocalDate.of(2023,12,7));
            assertEquals(period.getDays()<7,true);
        }
        //From now on,the following test cases are failure cases
        //#3:Malicious span
        weeks = glycemiaController.LookupChartRecord("1", "mess", "2023-12-27");
        assertEquals(weeks.getStatusCode(),HttpStatus.BAD_REQUEST);
        //#4:Fix case of letter
        weeks = glycemiaController.LookupChartRecord("1", "Week", "2023-12-27");
        assertEquals(weeks.getStatusCode(),HttpStatus.OK);
        //#5
        weeks = glycemiaController.LookupChartRecord("1", "Month", "2023-12-27");
        assertEquals(weeks.getStatusCode(),HttpStatus.OK);
        //#6
        weeks = glycemiaController.LookupChartRecord("1", "week", null);
        assertEquals(weeks.getStatusCode(),HttpStatus.BAD_REQUEST);
        //#7
        weeks = glycemiaController.LookupChartRecord("1", null, null);
        assertEquals(weeks.getStatusCode(),HttpStatus.BAD_REQUEST);
    }
}
