package edu.tongji.backend;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 Victor Hu,UltraTempest10
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





import cn.hutool.log.Log;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.mapper.UserMapper;
import edu.tongji.backend.service.impl.UserServiceImpl;
import edu.tongji.backend.controller.LoginController;
import edu.tongji.backend.dto.LoginFormDTO;
import edu.tongji.backend.util.PhoneGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import javax.annotation.Resource;

import static edu.tongji.backend.util.RedisConstants.LOGIN_CODE_TIMEOUT;
import static edu.tongji.backend.util.RedisConstants.LOGIN_LIMIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@Slf4j
public class PhoneLoginTest {
    @Autowired
    LoginController loginController;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserServiceImpl userService;
    private MockMvc mockMvc;
    private MockHttpSession session;

    //在每个测试方法执行之前都初始化MockMvc对象
    @BeforeEach
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        this.session = new MockHttpSession();
    }
    @Test
    void SendCaptchaBatch() throws Exception {
        log.debug("[1] send captcha (Batch)");
        sendCaptcha("15555555555",false);
        sendCaptcha("13655321254",false);
    }
    @Test
    void LoginFrozenBatch() throws Exception {
        stringRedisTemplate.opsForValue().set(LOGIN_LIMIT + "15204808552", String.valueOf(5));
        //NOTE:ONLY FOR TEST!
        for(int i=0;i<7;i++)
            LoginError("15204808552","89",status().is4xxClientError());
        LoginError("15204808552","04808552",status().is4xxClientError());
        stringRedisTemplate.opsForValue().set(LOGIN_LIMIT + "15204808552", String.valueOf(5));
    }
    @Test
    void LoginFrozenBatchIII() throws Exception {
        stringRedisTemplate.delete(LOGIN_LIMIT + "15204808552");
        //NOTE:ONLY FOR TEST!
        for(int i=0;i<2;i++)
            LoginError("15204808552","89",status().is4xxClientError());
        LoginError("15204808552","04808552",status().isOk());
        stringRedisTemplate.opsForValue().set(LOGIN_LIMIT + "15204808552", String.valueOf(5));
    }
    @Test
    void LoginFrozenBatchII() throws Exception {
        stringRedisTemplate.opsForValue().set(LOGIN_LIMIT + "15204808552", String.valueOf(-1));
        //NOTE:ONLY FOR TEST!
        for(int i=0;i<2;i++)
            LoginError("15204808552","89",status().is4xxClientError());
        LoginError("15204808552","04808552",status().is4xxClientError());
        stringRedisTemplate.opsForValue().set(LOGIN_LIMIT + "15204808552", String.valueOf(5));
    }
    @Test
    void LoginFrozenBatchIV() throws Exception {
        stringRedisTemplate.opsForValue().set(LOGIN_LIMIT + "15204808552", String.valueOf(-1));
        //NOTE:ONLY FOR TEST!
        for(int i=0;i<2;i++)
            LoginError(null,"89",status().is4xxClientError());
        LoginError(null,null,status().is4xxClientError());
        stringRedisTemplate.opsForValue().set(LOGIN_LIMIT + "15204808552", String.valueOf(5));
    }
    @Test
    void LoginPassBatch() throws Exception {
        stringRedisTemplate.opsForValue().set(LOGIN_LIMIT + "15204808552", String.valueOf(5));
        LoginError("15204808552","04808552",status().isOk());
    }
    @Test
    void LoginErrorViaPhoneBatch() throws Exception{
        //Actually it's not expired ,it's just for assertion of false
        stringRedisTemplate.delete(LOGIN_LIMIT + "15555555555");
        testWithCaptcha(false, false, "15555555555");
        stringRedisTemplate.opsForValue().set(LOGIN_LIMIT+"15555555555","-1");
        testWithCaptcha(false, false, "15555555555");

    }
    @Test
    void LoginErrorBatch() throws Exception {
        stringRedisTemplate.opsForValue().set(LOGIN_LIMIT + "88", String.valueOf(5));
        for(int i=0;i<7;i++)
            LoginError("88","89",status().is4xxClientError());
        LoginError("88",null,status().is4xxClientError());
        stringRedisTemplate.opsForValue().set(LOGIN_LIMIT + "88", String.valueOf(5));
        stringRedisTemplate.opsForValue().set(LOGIN_LIMIT + "-1", String.valueOf(5));
        //does not exist
        LoginError("-1","89",status().is4xxClientError());
        //param error
        LoginError("contact",null,status().is4xxClientError());
    }
    //Only wrong password can appear
    void LoginError(String contact, String password, ResultMatcher res) throws Exception{
        User user=new User();
        user.setContact(contact);
        user.setPassword(password);
        String jsonString = JSONObject.toJSONString(user);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/login/pass")
                .param("user",jsonString)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .content(jsonString)
                .contentType("application/json;charset=UTF-8")
        ).andExpect(res);//Default:status().isOk()
    }
    Integer sendCaptcha(String contact,Boolean verbose) throws Exception {
        return sendCaptcha(contact,verbose,false);
    }
    Integer sendCaptcha(String contact,Boolean verbose,Boolean malicious) throws Exception {
        return sendCaptcha(mockMvc,contact,verbose,malicious);
    }
    Integer sendCaptcha(MockMvc mockMvc,String contact,Boolean verbose) throws Exception {
        return sendCaptcha(mockMvc,contact,verbose,false);
    }
    //@Test
    Integer sendCaptcha(MockMvc mockMvc,String contact,Boolean verbose,Boolean malicious) throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/login/captcha")
                .param("contact", contact)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .content(contact!=null?contact:"")
                .contentType("application/text;charset=UTF-8")
        );
        if(malicious)
            result.andExpect(status().is4xxClientError());
        else
            result.andExpect(status().isOk());
        MvcResult fresult;
        if(verbose)
            fresult=result.andDo( print()).andReturn();
        else
            fresult=result.andReturn();
        try {
            Integer captcha = Integer.parseInt(fresult.getRequest().getSession().getAttribute("Captcha").toString());
            return captcha;
        }catch (NumberFormatException|NullPointerException e){
            log.error(e.getMessage());
            return -1;
        }
    }
    @Test
    void TestNewContact() throws Exception {
        String tel="";
        do {
            tel = PhoneGenerator.getTel();
        }while(loginController.repeatedContact(tel).getResponse());
        System.out.println(tel);
        testWithCaptcha(false,false,tel);
    }
    @Test
    void testWithoutCaptchaBatch() throws Exception {
        stringRedisTemplate.opsForValue().set(LOGIN_LIMIT + "15555555555", String.valueOf(-1));
        testWithoutCaptcha("000000");
        stringRedisTemplate.delete(LOGIN_LIMIT + "15555555555");
        testWithoutCaptcha("000000");
        stringRedisTemplate.delete(LOGIN_LIMIT + "15555555555");
        testWithoutCaptcha(null,null);
        testWithoutCaptcha("132 2451 2136",null);//Invalid Phone format
        stringRedisTemplate.opsForValue().set(LOGIN_LIMIT + "15555555555", String.valueOf(5));
        testWithoutCaptcha("123456");
        stringRedisTemplate.delete(LOGIN_LIMIT + "15555555555");
        testWithoutCaptcha();
    }
    void testWithoutCaptcha(String code) throws Exception {
        testWithoutCaptcha("15555555555",code);
    }
    void testWithoutCaptcha() throws Exception {
        testWithoutCaptcha("15555555555",null);
    }
    @Test
    void sendCodeToGhost() throws Exception {
        log.debug("Test invalid contact format (5/5)");
        sendCaptcha(";drop table test;--",false,true);
        sendCaptcha("THEUNITEDKINGDOM",false,true);
        sendCaptcha("1331234123",false,true);
        sendCaptcha("130 1234 1234",false,true);
        sendCaptcha("021-1234-6579",false,true);
    }
    void testWithoutCaptcha(String contact,String code) throws Exception {
        log.debug("[2] test without captcha ");
        Integer realcode;
        //Must be Wrong Captcha
        if(code!=null){
            realcode = sendCaptcha(contact, false);
            code= code.substring(0, 3);
        }
        boolean verbose=false;
        LoginFormDTO user=new LoginFormDTO(contact,code,null);
        String jsonResult= JSONObject.toJSONString(user);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/login/phone")
                        .param("loginForm", jsonResult)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .content(jsonResult)
                        .contentType("application/json;charset=UTF-8")
                ).andExpect(status().is4xxClientError());
        if(verbose)
            result.andDo( print()).andReturn();
        else
            result.andReturn();
    }
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Test
    void DirectSign() throws Exception {
        //String token = testWithCaptcha(false, false);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/login/sign")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().is4xxClientError());
    }
    @Test
    void OnlySignCount() throws Exception {
        String token = testWithCaptcha(false, false,"13245678909");
        //Get the consecutive count directly
        ResultActions result2 = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/login/sign/count")
                .header("authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletResponse response = result2.andReturn().getResponse();
        JsonNode jsonNode = objectMapper.readTree(response.getContentAsString());
        Integer Afterdata = jsonNode.get("response").asInt();
        System.out.println("Afterdata :"+(Afterdata));
        assertEquals (Afterdata,0);
    }
    @Test
    void LoginThenSign() throws Exception {
        String token = testWithCaptcha(false, false);
        //Get the consecutive count #1
        ResultActions result1 = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/login/sign/count")
                .header("authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
        MockHttpServletResponse response = result1.andReturn().getResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getContentAsString());
        Integer Beforedata = jsonNode.get("response").asInt();
        //Sign
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/login/sign")
                .header("authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
        String signMsg = result.andReturn().getResponse().getContentAsString();
        jsonNode = objectMapper.readTree(signMsg);
        Boolean repeated = (jsonNode.get("response").asInt()==2);
        //Get the consecutive count again
        ResultActions result2 = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/login/sign/count")
                .header("authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
        response = result2.andReturn().getResponse();
        jsonNode = objectMapper.readTree(response.getContentAsString());
        Integer Afterdata = jsonNode.get("response").asInt();
        System.out.println("Before data:"+Beforedata);
        System.out.println("Afterdata - 1"+(Afterdata-1));
        if(!repeated)
            assertEquals (Beforedata,Afterdata-1);
        else
            assertEquals (Beforedata,Afterdata);
    }
    @Test
    void LoginThenGetInfo() throws Exception{
        String token = testWithCaptcha(false, false);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/login/me")
                .header("authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
    }
    //TODO:sendCode+testCaptcha
    @Test
    void testWithCaptchaBatch() throws Exception{
        log.debug("[3] test with captcha (Batch 2/2)");
        log.debug("[3.1] test with expired captcha");
        //testWithCaptcha(true,false);
        log.debug("[3.2] test with effective captcha");
        testWithCaptcha(false,false);
    }
    public String testWithCaptcha(boolean expire,boolean verbose) throws Exception{
        return testWithCaptcha(mockMvc,expire,verbose,"15555555555");
    }
    public String testWithCaptcha(MockMvc mockMvc,boolean expire,boolean verbose) throws Exception{
        return testWithCaptcha(mockMvc,expire,verbose,"15555555555");
    }
    public String testWithCaptcha(boolean expire,boolean verbose,String contact) throws Exception{
        return testWithCaptcha(mockMvc,expire,verbose,contact);
    }
    //Return authorization
    public String testWithCaptcha(StringRedisTemplate stringRedisTemplate,MockMvc mockMvc,Boolean expire,boolean verbose,String contact) throws Exception {
        stringRedisTemplate.delete(LOGIN_LIMIT+contact);
        stringRedisTemplate.opsForValue().set(LOGIN_LIMIT + contact, String.valueOf(5));
        return testWithCaptcha(mockMvc,expire,verbose,contact);
    }
    public String testWithCaptcha(StringRedisTemplate stringRedisTemplate,MockMvc mockMvc,Boolean expire,boolean verbose) throws Exception {
        String contact="15555555555";
        stringRedisTemplate.delete(LOGIN_LIMIT+contact);
        stringRedisTemplate.opsForValue().set(LOGIN_LIMIT + contact, String.valueOf(5));
        return testWithCaptcha(mockMvc,expire,verbose,contact);
    }
    public String testWithCaptcha(MockMvc mockMvc,Boolean expire,boolean verbose,String contact) throws Exception {
        if(stringRedisTemplate!=null){
            stringRedisTemplate.delete(LOGIN_LIMIT+contact);
            stringRedisTemplate.opsForValue().set(LOGIN_LIMIT + contact, String.valueOf(5));
        }
        //expire is actually a kind of malicious activity
        Integer captcha = sendCaptcha(mockMvc,contact, false,expire);
        //If get captcha failed
        if(captcha.equals(-1)){
            return "-1";//NO TOKEN
        }
        /*if(expire){
            Thread.sleep(LOGIN_CODE_TIMEOUT*60*1000);
        }*/
        LoginFormDTO user=new LoginFormDTO(contact,captcha.toString(),null);
        String jsonResult= JSONObject.toJSONString(user);
        ResultActions raw = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/login/phone")
                .param("loginForm", jsonResult)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .content(jsonResult)
                .contentType("application/json;charset=UTF-8")
        );
        MvcResult fresult2;
        if(!expire)
            raw=raw.andExpect(jsonPath("$.success", is(true)));
        else
            raw=raw.andExpect(jsonPath("$.success", is(false)));
        if(verbose)
            fresult2=raw.andDo( print()).andReturn();
        else
            fresult2=raw.andReturn();
        String token = fresult2.getRequest().getSession().getAttribute("authorization").toString();
        log.info(token);
        return token;
    }
}
