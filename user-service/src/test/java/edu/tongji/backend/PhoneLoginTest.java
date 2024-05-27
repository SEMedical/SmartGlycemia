package edu.tongji.backend;

import cn.hutool.log.Log;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.service.impl.UserServiceImpl;
import edu.tongji.backend.controller.LoginController;
import edu.tongji.backend.dto.LoginFormDTO;
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
    void LoginErrorBatch() throws Exception {
        for(int i=0;i<5;i++)
            LoginError("88","89",status().is4xxClientError());
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
    //@Test
    void sendCaptcha(String contact,Boolean verbose) throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/login/captcha")
                .param("contact", contact)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .content(contact)
                .contentType("application/text;charset=UTF-8")
        ).andExpect(status().isOk());
        if(verbose)
            result.andDo( print()).andReturn();
        else
            result.andReturn();
    }
    @Test
    void testWithoutCaptcha() throws Exception {
        log.debug("[2] test without captcha ");
        boolean verbose=false;
        LoginFormDTO user=new LoginFormDTO("15555555555",null,null);
        String jsonResult= JSONObject.toJSONString(user);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/login/phone")
                .param("loginForm", jsonResult)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .content(jsonResult)
                .contentType("application/json;charset=UTF-8")
        ).andExpect(status().is4xxClientError()).
                andExpect(content().json("{\"success\":false,\"message\":\"手机号或验证码为空\"}"));
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
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/login/captcha")
                .param("contact", contact)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .content(contact)
                .contentType("application/text;charset=UTF-8")
        ).andExpect(status().isOk());
        MvcResult fresult;
        if(verbose)
            fresult=result.andDo( print()).andReturn();
        else
            fresult=result.andReturn();
        Integer captcha = Integer.parseInt(fresult.getRequest().getSession().getAttribute("Captcha").toString());

        if(expire){
            Thread.sleep(LOGIN_CODE_TIMEOUT*60*1000);
        }
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
            raw=raw.andExpect(content().json("{\"success\":true}"));
        else
            raw=raw.andExpect(content().json("{\"success\":false,\"errorMsg\":\"verification failed\"}"));
        if(verbose)
            fresult2=raw.andDo( print()).andReturn();
        else
            fresult2=raw.andReturn();
        String token = fresult2.getRequest().getSession().getAttribute("authorization").toString();
        log.info(token);
        return token;
    }
}
