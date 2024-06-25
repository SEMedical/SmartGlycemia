package edu.tongji.backend.controller;

import cn.hutool.captcha.generator.RandomGenerator;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tongji.backend.dto.DoctorDTO1;
import edu.tongji.backend.dto.HospitalDTO;
import edu.tongji.backend.entity.Hospital;
import edu.tongji.backend.service.impl.AccountServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.ComparisonFailure;
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
import java.util.*;

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
    private static List<Integer> getRandomNumber(int count) {
        // 使用SET以此保证写入的数据不重复
        List<Integer> set = new ArrayList<Integer>();
        // 随机数
        Random random = new Random();

        while (set.size() < count) {
            // nextInt返回一个伪随机数，它是取自此随机数生成器序列的、在 0（包括）
            // 和指定值（不包括）之间均匀分布的 int 值。
            set.add(random.nextInt(10));
        }
        return set;
    }
    private static String generatedcode(int count) {
        List<Integer> set = getRandomNumber(count);
        // 使用迭代器
        Iterator<Integer> iterator = set.iterator();
        // 临时记录数据
        String temp = "";
        while (iterator.hasNext()) {
            temp += iterator.next();

        }
        return temp;
    }
    @Test
    void testGetAllHospitals() throws Exception {
        String token="srtbm,glb15vx15vz0cs15v15v1";
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
        Map<String,String> maps=new HashMap<>();
        maps.put("name","momo");
        maps.put("id","0");
        maps.put("role","admin");
        stringRedisTemplate.opsForHash().putAll(LOGIN_TOKEN_KEY+token,maps);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/oa/getAccountList")
                .header("authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
    }
    @Test
    void addDoctorBatch() throws Exception {

        try {
            addDoctorTest(2, "32072120020908421", "Ear,Nose,Throat", "director", "/data/0001.jpg", "02165990007", true);
            //OK
            Integer i = addDoctorTest(2, "350721200209084214", "Ear,Nose,Throat", "director", "/data/0001.jpg", "057165990007", false);
            removeDoctorTest(i, false);
            addDoctorTest(2, "320721090113744217", "Ear,Nose,Throat", "director", "/data/0001.jpg", "02165990007", true);
        }catch (feign.RetryableException e){
            System.out.println(e.getMessage());
        }

    }
    void removeDoctorTest(int userId,Boolean malicious) throws Exception {
        String token="srtbm,glb15vx15vz0cs15v15v1";
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
        Map<String,String> maps=new HashMap<>();
        maps.put("name","momo");
        maps.put("id","0");
        maps.put("role","admin");
        stringRedisTemplate.opsForHash().putAll(LOGIN_TOKEN_KEY+token,maps);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/oa/deleteAccount")
                .param("doctor_id", String.valueOf(userId))
                .content("application/text;charset=UTF-8")
                .header("Authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        );
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
        if(malicious)
            result.andExpect(status().isOk());
        else
            result.andExpect(status().isOk());
    }
    Integer addDoctorTest(int hospitalId,String idCard,String department,String title,String photoPath,String contact,Boolean malicious) throws Exception {
        String token="srtbm,glb15vx15vz0cs15v15v1";
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
        Map<String,String> maps=new HashMap<>();
        maps.put("name","momo");
        maps.put("id","0");
        maps.put("role","admin");
        stringRedisTemplate.opsForHash().putAll(LOGIN_TOKEN_KEY+token,maps);
        DoctorDTO1 doctor=new DoctorDTO1(hospitalId,"momo",idCard,department,title,photoPath,contact);
        String jsonString = JSONObject.toJSONString(doctor);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/oa/addAccount")
                .content(jsonString)
                .contentType("application/json")
                .header("Authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        );
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
        result.andExpect(status().isOk());

        String contentAsString = result.andReturn().getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(contentAsString);
        String message = jsonNode.get("message").toString();
        if(!malicious) {
            String response = jsonNode.get("response").toString();
            response = removeFirstAndLastChar(response);
            Integer i = Integer.valueOf(response);
            assertEquals(result.andReturn().getResponse().getStatus(), HttpStatus.OK.value());
            return i;
        }else{
            try {
                assertEquals(message, "\"the length of ID must be 18 or 15\"");
            }catch (ComparisonFailure e){
                assertEquals(message,"\"Text '"+idCard.substring(6,14)+"' could not be parsed: Invalid value for MonthOfYear (valid values 1 - 12): 13\"");
            }
            assertEquals(result.andReturn().getResponse().getStatus(), HttpStatus.OK.value());
            return -1;
        }
    }
    @Test
    void addHospitalBatch() throws Exception {
        //Repeated Hospital Name
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String code = generatedcode(6);
            addHospitalTest("瑞金医院", "三甲", "嘉定区曹安公路1", new BigDecimal("30"),
                    new BigDecimal("120"), "200062", "120001",
                    "8:00-17:00", "测试重复的hospitalName", true);
            Integer id = addHospitalTest("瑞金医院" + code, "三甲", "嘉定区曹安公路1" + code, new BigDecimal("30"),
                    new BigDecimal("120"), "200062", code,
                    "8:00-17:00", "测试重复的hospitalName", false);
            TestUnregisterHospital(id, false);
        }catch (feign.RetryableException e){
            System.out.println(e.getMessage());
        }
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
            result.andExpect(status().isOk());
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
            assertEquals(result.andReturn().getResponse().getStatus(), HttpStatus.OK.value());
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
