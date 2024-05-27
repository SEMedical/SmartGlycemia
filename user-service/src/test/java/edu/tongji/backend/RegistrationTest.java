package edu.tongji.backend;

import com.alibaba.fastjson.JSONObject;
import edu.tongji.backend.controller.RegisterController;
import edu.tongji.backend.dto.RegisterDTO;
import edu.tongji.backend.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

import static edu.tongji.backend.util.RedisConstants.LOGIN_LIMIT;
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
    void testRegistrationBatch() throws Exception{
        //name,password,contact,gender,age
        testRegister("Eve","Serpent!1234","13101000002","Female",2024);
        testUnregister("13101000002");
    }
    void testRegister(String name,String password,String contact,String gender,Integer age) throws Exception {
        RegisterDTO registerDTO=new RegisterDTO(name,password,contact,gender,age);
        testRegister(registerDTO);
    }
    void testRegister(RegisterDTO registerDTO) throws Exception{
        String jsonResult= JSONObject.toJSONString(registerDTO);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/register/patient")
                .param("info",jsonResult)
                .content(jsonResult)
                .contentType("application/json;charset=UTF-8")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
    }

    void testUnregister(String contact) throws Exception {
        String token = phoneLoginTest.testWithCaptcha(stringRedisTemplate,mockMvc,false, false,contact);
        ResultActions result2 = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/register/unregister")
                .header("authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
    }
}
