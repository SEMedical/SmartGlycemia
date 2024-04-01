package edu.tongji.backend;

import com.alibaba.fastjson.JSONObject;
import edu.tongji.backend.controller.LoginController;
import edu.tongji.backend.dto.LoginFormDTO;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import static edu.tongji.backend.util.RedisConstants.LOGIN_CODE_TIMEOUT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
public class PhoneLoginTest {
    @Autowired
    LoginController loginController;
    @Autowired
    private WebApplicationContext webApplicationContext;
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
        sendCaptcha("15555555555",false);
        sendCaptcha("13655321254",false);
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
        LoginFormDTO user=new LoginFormDTO("15555555555",null,null);
        String jsonResult= JSONObject.toJSONString(user);
        MvcResult result= mockMvc.perform(MockMvcRequestBuilders
                .post("/api/login/phone")
                .param("loginForm",jsonResult)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .content(jsonResult)
                .contentType("application/json;charset=UTF-8")
        )./*andExpect(status().is4xxClientError()).*/
                andExpect(content().json("{\"success\":false,\"errorMsg\":\"verification failed\"}")).andDo(print()).andReturn();
    }
    //TODO:sendCode+testCaptcha
    @Test
    void testWithCaptchaBatch() throws Exception{
        testWithCaptcha(true);
        testWithCaptcha(false);
    }
    void testWithCaptcha(boolean expire) throws Exception {
        String contact= "15555555555";
        MvcResult result= mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/login/captcha")
                        .param("contact",contact)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .content(contact)
                        .contentType("application/text;charset=UTF-8")
                ).andExpect(status().isOk())
                .andDo(print()).andReturn();
        Integer captcha = Integer.parseInt(result.getRequest().getSession().getAttribute("Captcha").toString());
        System.out.println(captcha);
        if(expire){
            Thread.sleep(LOGIN_CODE_TIMEOUT*60*1000);
        }
        LoginFormDTO user=new LoginFormDTO("15555555555",captcha.toString(),null);
        String jsonResult= JSONObject.toJSONString(user);
        ResultActions raw = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/login/phone")
                .param("loginForm", jsonResult)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .content(jsonResult)
                .contentType("application/json;charset=UTF-8")
        );
        if(!expire)
            raw.andExpect(content().json("{\"success\":true}")).andDo(print()).andReturn();
        else
            raw.andExpect(content().json("{\"success\":false,\"errorMsg\":\"verification failed\"}")).andDo(print()).andReturn();
    }
}
