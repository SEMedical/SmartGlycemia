package edu.tongji.backend;

import cn.hutool.log.Log;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tongji.backend.controller.LoginController;
import edu.tongji.backend.controller.RegisterController;
import edu.tongji.backend.dto.RegisterDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.mapper.UserMapper;
import edu.tongji.backend.service.impl.UserServiceImpl;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

import static edu.tongji.backend.util.RedisConstants.LOGIN_LIMIT;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Slf4j
public class RegistrationTest {
    @Autowired
    RegisterController registerController;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private MockHttpSession session;
    private PhoneLoginTest phoneLoginTest=new PhoneLoginTest();
    @Resource
    StringRedisTemplate stringRedisTemplate;
    //在每个测试方法执行之前都初始化MockMvc对象
    @BeforeEach
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        this.session = new MockHttpSession();
    }
    @Test
    void testRegistrationPassBatch() throws Exception{
        //name,password,contact,gender,age
        testRegister("Eve","Serpent!1234","13101000002","Female",2024);
        testUnregister("13101000002");
    }
    @Test
    void testRegistrationErrorBatch() throws Exception{
        //name,password,contact,gender,age
        //#1: Wrong format password
        testRegister("Eve","Serpent","13101000002","Female",2024,true);
        //#2:Wrong format contact
        testRegister("Eve","Serpent!1234","131 0100 0002","Female",2024,true);
        //#3: Must be pure Chinese/English
        testRegister("夏娃Eve","Serpent!1234","13101000002","Female",2024,true);
        //#4:Limited to 10-digit length
        testRegister("亚伯拉罕·摩西·耶和华·特洛伊","Serpent!1234","13101000002","Female",2024,true);
        //#5:Empty contact/password
        testRegister("Eve",null,"13101000002","Female",2024,true);
        //#6 ,same as #5
        testRegister(null,"Serpent!1234","13101000002","Female",2024,true);
        //#7,repeated contact
        testRegister("Eve","Serpent!1234","13745678909","Female",2024,true);
        //#8
        testRegister("Eve","Serpent!1234",null,"Female",2024,true);
    }
    void testRegister(String name,String password,String contact,String gender,Integer age) throws Exception {
        testRegister(name,password,contact,gender,age,false);
    }
    void testRegister(String name,String password,String contact,String gender,Integer age,Boolean malicious) throws Exception {
        RegisterDTO registerDTO=new RegisterDTO(name,password,contact,gender,age);
        testRegister(registerDTO,malicious);
    }
    void testRegister(RegisterDTO registerDTO) throws Exception {
        testRegister(registerDTO,false);
    }
    void testRegister(RegisterDTO registerDTO,Boolean malicious) throws Exception{
        String jsonResult= JSONObject.toJSONString(registerDTO);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/register/patient")
                .param("info",jsonResult)
                .content(jsonResult)
                .contentType("application/json;charset=UTF-8")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        );
        if(!malicious)
            result.andExpect(status().isOk());
        else
            result.andExpect(status().is4xxClientError());
    }

    void testUnregister(String contact) throws Exception {
        String token = phoneLoginTest.testWithCaptcha(stringRedisTemplate,mockMvc,false, false,contact);
        ResultActions result2 = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/register/unregister")
                .header("authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
    }
    @Autowired
    UserMapper userMapper;
    @Autowired
    LoginController loginController;
    @Test
    void InvalidaddrmUserForOASuite() throws Exception {
        Integer doctorId=userMapper.getMaxUserId();
        String address="上海市杨浦区杨树浦路";
        String contact= PhoneGenerator.getTel();
        String defaultPassword="a109e36947ad56de1dca1cc49f0ef8ac9ad9a7b1aa0df41fb3c4cb73c1ff01ea";
        User user = new User(doctorId, address, "Alice", contact, defaultPassword, "patient");//Deliberately
        String jsonResult= JSONObject.toJSONString(user);
        Boolean flag1=false;
        try {
            ResultActions result1 = mockMvc.perform(MockMvcRequestBuilders
                    .post("/api/register/addUser")
                    .param("user", jsonResult)
                    .accept(MediaType.parseMediaType("application/text;charset=UTF-8"))
                    .content(jsonResult)
                    .contentType("application/json;charset=UTF-8")
            );
        }catch (Exception e){
            flag1=true;
        }
        assertEquals(flag1,true);
        flag1=false;
        try {
            ResultActions result2 = mockMvc.perform(MockMvcRequestBuilders
                    .post("/api/register/rmUser")
                    .param("userId", String.valueOf(doctorId))
                    .accept(MediaType.parseMediaType("application/text;charset=UTF-8"))
            );
        }catch (Exception e){
            flag1=true;
        }
        assertEquals(flag1,true);
    }
    @Test
    void addrmUserForOASuite() throws Exception {
        Integer doctorId=userMapper.getMaxUserId()+1;
        String address="上海市杨浦区杨树浦路";
        String contact= PhoneGenerator.getTel();
        String defaultPassword="a109e36947ad56de1dca1cc49f0ef8ac9ad9a7b1aa0df41fb3c4cb73c1ff01ea";
        User user = new User(doctorId, address, "Alice", contact, defaultPassword, "patient");//Deliberately
        String jsonResult= JSONObject.toJSONString(user);
        ResultActions result1 = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/register/addUser")
                .param("user",jsonResult)
                .accept(MediaType.parseMediaType("application/text;charset=UTF-8"))
                .content(jsonResult)
                .contentType("application/json;charset=UTF-8")
        ).andExpect(status().isOk());
        assertEquals(loginController.repeatedContact(contact).getResponse(),true);
        ResultActions result2 = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/register/rmUser")
                .param("userId",String.valueOf(doctorId))
                .accept(MediaType.parseMediaType("application/text;charset=UTF-8"))
        ).andExpect(status().isOk());
        assertEquals(loginController.repeatedContact(contact).getResponse(),false);
    }
}
