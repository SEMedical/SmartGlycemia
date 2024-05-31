package edu.tongji.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tongji.backend.service.impl.AccountServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static edu.tongji.backend.util.RedisConstants.LOGIN_TOKEN_KEY;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Slf4j
public class MockTest {
    @Autowired
    AccountController accountController;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private AccountServiceImpl accountService;
    private MockMvc mockMvc;
    private MockHttpSession session;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @BeforeEach
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        this.session = new MockHttpSession();
    }
    @Test
    void addHospitalBatch() throws Exception {
        //Repeated Hospital Name
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        addHospitalTest("瑞金医院","三甲","嘉定区曹安公路1",new BigDecimal("30"),
                new BigDecimal("120"),"200062","120001",
                "8:00-17:00","测试重复的hospitalName",true);
        Integer id = addHospitalTest("瑞金医院"+ timestamp, "三甲", "嘉定区曹安公路1"+timestamp, new BigDecimal("30"),
                new BigDecimal("120"), "200062", timestamp.toString().substring(0,7),
                "8:00-17:00", "测试重复的hospitalName", false);
        TestUnregisterHospital(id,false);
    }
    void TestUnregisterHospital(Integer id,Boolean malicious) throws Exception {
        String token="srtbm,glb15vx15vz0cs15v15v1";
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
        Map<String,String> maps=new HashMap<>();
        maps.put("name","momo");
        maps.put("id","0");
        maps.put("role","admin");
        stringRedisTemplate.opsForHash().putAll(LOGIN_TOKEN_KEY+token,maps);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/oa/removeHospital")
                .param("hospital_id",id.toString())
                .header("authorization",token)
                .content("application/json;charset=UTF-8")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        );
        if(malicious)
            result.andExpect(status().isBadRequest());
        else
            result.andExpect(status().isOk());
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
    }
    Integer addHospitalTest(String hospitalName,String level,String address,BigDecimal latitude,
                         BigDecimal longitude,String zipcode,String hospitalPhone
    ,String outpatientHours,String introduction,Boolean malicious) throws Exception {
        //Simulate Login
        String token="srtbm,glb15vx15vz0cs15v15v1";
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
        Map<String,String> maps=new HashMap<>();
        maps.put("name","momo");
        maps.put("id","0");
        maps.put("role","admin");
        stringRedisTemplate.opsForHash().putAll(LOGIN_TOKEN_KEY+token,maps);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/oa/addHospital")
                .param("hospital_name",hospitalName)
                .param("level",level)
                .param("address",address)
                .param("latitude", String.valueOf(latitude))
                .param("longitude", String.valueOf(longitude))
                .param("zipcode",zipcode)
                .param("hospital_phone",hospitalPhone)
                .param("outpatient_hour",outpatientHours)
                .param("introduction",introduction)
                .header("authorization",token)
                .content("application/json;charset=UTF-8")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        );
        if(malicious)
            result.andExpect(status().isBadRequest());
        else
            result.andExpect(status().isOk());
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
        return handleAddHospital(result,malicious);
    }
    private static String removeFirstAndLastChar(String str) {
        // 检查字符串是否为空或者长度小于2
        if (str == null || str.length() < 2) {
            return str;
        }
        // 返回去掉第一个和最后一个字符后的新字符串
        return str.substring(1, str.length() - 1);
    }
    Integer handleAddHospital(ResultActions result,Boolean malicious) throws JsonProcessingException, UnsupportedEncodingException {
        String contentAsString = result.andReturn().getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(contentAsString);
        String message = jsonNode.get("message").toString();

        if(malicious) {
            assertEquals(message, "\"The hospital phone/name/address might have been used before!\"");
            assertEquals(result.andReturn().getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
            return -1;
        }else{
            String response = jsonNode.get("response").toString();
            response = removeFirstAndLastChar(response);
            Integer i = Integer.valueOf(response);
            assertEquals(result.andReturn().getResponse().getStatus(), HttpStatus.OK.value());
            return i;
        }
    }
}
