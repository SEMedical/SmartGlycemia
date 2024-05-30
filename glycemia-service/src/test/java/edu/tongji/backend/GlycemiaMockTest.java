package edu.tongji.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tongji.backend.controller.GlycemiaController;
import edu.tongji.backend.dto.UserDTO;
import edu.tongji.backend.service.impl.GlycemiaServiceImpl;
import edu.tongji.backend.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
public class GlycemiaMockTest {
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
    void TestGlycemiaChartsBatch() throws Exception {
        TestGlycemiaCharts("Realtime","2024-01-02",status().isOk());
        TestGlycemiaCharts("History","2024-01-02",status().isOk());
    }

    void TestGlycemiaCharts(String type, String date, ResultMatcher matcher) throws Exception {
        UserDTO userDTO=new UserDTO(null,"小帅","1","patient");
        UserHolder.saveUser(userDTO);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/glycemia/chart")
                .param("type",type)
                .param("date",date)
                .content("application/text;charset=UTF-8")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(matcher);//Default:status().isOk()
        UserHolder.removeUser();
    }
    @Test
    void TestWeeklyOrMonthlyRecordBatch() throws Exception {
        TestWeeklyOrMonthlyRecord("month", "2023-12-27",status().isOk());
        TestWeeklyOrMonthlyRecord("week", null,status().isBadRequest());
    }
    void TestWeeklyOrMonthlyRecord(String type, String date, ResultMatcher matcher) throws Exception {
        UserDTO userDTO=new UserDTO(null,"小帅","1","patient");
        UserHolder.saveUser(userDTO);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/glycemia/weeklyOrMonthlyRecord")
                .param("span",type)
                .param("startDate", date)
                .contentType("application/text;charset=UTF-8")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(matcher);//Default:status().isOk()
        UserHolder.removeUser();
    }
    @Test
    void TestRealTimeBatch() throws Exception{
        TestRealTime(status().isOk());
    }
    void TestRealTime(ResultMatcher matcher) throws Exception {
        UserDTO userDTO=new UserDTO(null,"小帅","1","patient");
        UserHolder.saveUser(userDTO);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/glycemia/realTime")
                .header("authorization","rgbtsghnjbzsvjkv14f5154gscscczs5")
                .content("application/text;charset=UTF-8")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(matcher);//Default:status().isOk()
        UserHolder.removeUser();
    }
    @Test
    void TestRealTimePromptBatch() throws Exception{
        TestRealTimePrompt(status().isServiceUnavailable());
    }
    void TestRealTimePrompt(ResultMatcher matcher) throws Exception {
        UserDTO userDTO=new UserDTO(null,"小帅","1","patient");
        UserHolder.saveUser(userDTO);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/glycemia/realTimePrompt")
                .content("application/text;charset=UTF-8")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(matcher);//Default:status().isOk()
        UserHolder.removeUser();
    }
    @Test
    void TestDailyHistory() throws Exception {
        TestDailyHistory("1024-01-02",status().isBadRequest());
        TestDailyHistory("2024-01-02",status().isOk());
    }
    void TestDailyHistory(String date,ResultMatcher matcher) throws Exception {
        UserDTO userDTO=new UserDTO(null,"小帅","1","patient");
        UserHolder.saveUser(userDTO);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/glycemia/dailyHistory")
                .param("date",date)
                .content("application/text;charset=UTF-8")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(matcher);//Default:status().isOk()
        UserHolder.removeUser();
    }
}
