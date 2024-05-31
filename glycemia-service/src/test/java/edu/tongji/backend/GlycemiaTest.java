package edu.tongji.backend;

import com.alibaba.fastjson.JSONObject;
import edu.tongji.backend.controller.GlycemiaController;
import edu.tongji.backend.dto.*;
import edu.tongji.backend.service.impl.GlycemiaServiceImpl;
import edu.tongji.backend.util.RegexUtils;
import edu.tongji.backend.util.Response;
import edu.tongji.backend.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @Resource
    StringRedisTemplate stringRedisTemplate;
    //在每个测试方法执行之前都初始化MockMvc对象
    @BeforeEach
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        this.session = new MockHttpSession();
        stringRedisTemplate.delete("cache:history:glycemia:1:2023-12-09");
        stringRedisTemplate.delete("cache:glycemia:1:2024-01-02 09:45:00");
        stringRedisTemplate.delete("cache:user:last:exercise:1");
        stringRedisTemplate.delete("cache:running:3275");
        stringRedisTemplate.delete("cache:daily:glycemia:1:2024-01-02 11:00:00");
        stringRedisTemplate.delete("cache:glycemia:1:2024-01-01 02:45:00");
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
        tipResponse = glycemiaController.GetRealtimeTips("102", 21);
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
        assertEquals(response.getStatusCode(),HttpStatus.OK);
        response = glycemiaController.GetRealtimeGlycemia("Apple");
        assertEquals(response.getStatusCode(),HttpStatus.BAD_REQUEST);
        response= glycemiaController.GetRealtimeGlycemia("0");
        assertEquals(response.getStatusCode(),HttpStatus.BAD_REQUEST);
        //assertEquals(tipResponse.getResponse().getColor(),MyColor.RED);
    }
    @Test
    public void TestGetDailyCharts(){
        ResponseEntity<Response<DailyChart>> response = glycemiaController.GetDailyChart("1", "2024-01-02");
        assertEquals(response.getStatusCode(),HttpStatus.OK);
        DailyChart chart = response.getBody().getResponse();
        Map<LocalDateTime, Double> map = chart.getEntry().get(0);
        for (Map.Entry<LocalDateTime, Double> entry : map.entrySet()) {
            assertEquals(entry.getKey().toLocalDate().equals(LocalDate.of(2024,1,2)),true );
            break;
        }
        response = glycemiaController.GetDailyChart("1", "1024-01-02");
        assertEquals(response.getStatusCode(),HttpStatus.BAD_REQUEST);
        response = glycemiaController.GetDailyChart("1", "3024-01-02");
        assertEquals(response.getStatusCode(),HttpStatus.BAD_REQUEST);
        response = glycemiaController.GetDailyChart("1", "2024.01.02");
        assertEquals(response.getStatusCode(),HttpStatus.BAD_REQUEST);
    }
    @Test
    void TestIsExerciseBatch() throws Exception {
        TestIsExercise("Jogging","2024-01-02",status().isOk());
        TestIsExercise("Swimming","2024-01-02",status().isBadRequest());
        TestIsExercise("jogging","2024-01-02",status().isBadRequest());
        TestIsExercise("Yoga","2024-01-02",status().isOk());
        TestIsExercise("Yoga","2024-01.02",status().isBadRequest());
        TestIsExercise("Yoga","1024-01-02",status().isBadRequest());
        TestIsExercise("Yoga","3024-01-02",status().isBadRequest());
    }

    void TestIsExercise(String type,String date,ResultMatcher matcher) throws Exception {

        UserDTO userDTO=new UserDTO(null,"小帅","1","patient");
        UserHolder.saveUser(userDTO);
        String token="rgbtsghnjbzsvjkv14f5154gscscczs5";
        stringRedisTemplate.delete(token);
        Map<String,String> maps=new HashMap<>();
        maps.put("name","小帅");
        maps.put("role","patient");
        maps.put("userId","1");
        stringRedisTemplate.opsForHash().putAll(token,maps);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/glycemia/isExercise")
                .param("type",type)
                .param("date",date)
                .content("application/text;charset=UTF-8")
                .header("authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(matcher);//Default:status().isOk()
        UserHolder.removeUser();
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
