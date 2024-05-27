package edu.tongji.backend;

import com.alibaba.fastjson.JSONObject;
import edu.tongji.backend.controller.LoginController;
import edu.tongji.backend.controller.ProfileController;
import edu.tongji.backend.controller.RegisterController;
import edu.tongji.backend.dto.LoginFormDTO;
import edu.tongji.backend.dto.ProfileDTO;
import edu.tongji.backend.dto.RegisterDTO;
import edu.tongji.backend.service.impl.ProfileServiceImpl;
import edu.tongji.backend.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.NoSuchAlgorithmException;

import static edu.tongji.backend.util.RedisConstants.LOGIN_CODE_TIMEOUT;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Slf4j
@SpringBootTest
class ProfileTests {
    @Autowired
    LoginController loginController;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ProfileController profileController;
    @Autowired
    private ProfileServiceImpl profileService;
    private MockMvc mockMvc;
    private MockHttpSession session;
    private PhoneLoginTest phoneLoginTest=new PhoneLoginTest();
    //在每个测试方法执行之前都初始化MockMvc对象
    @BeforeEach
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        this.session = new MockHttpSession();
    }
    @Test
    public void TestGetHealthRecord() throws Exception {
        String token = phoneLoginTest.testWithCaptcha(mockMvc,false, false);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/health/health-record")
                .header("authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
        MockHttpServletResponse response = result.andReturn().getResponse();
        System.out.println(response.getContentAsString());
    }
    public void TestUpdateHealthRecord(ProfileDTO profile) throws Exception {
        String token = phoneLoginTest.testWithCaptcha(mockMvc,false, false);
        String jsonResult= JSONObject.toJSONString(profile);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/health/update-health-record")
                .param("profileDTO",jsonResult)
                .header("authorization",token)
                .content(jsonResult)
                .contentType("application/json;charset=UTF-8")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
        MockHttpServletResponse response = result.andReturn().getResponse();
        System.out.println(response.getContentAsString());
    }
    @Test
    public void TestUpdateHealthRecord() throws Exception {
        ProfileDTO profile=new ProfileDTO("Male",22,"170cm","110kg","II","Autism",2019,null);
        TestUpdateHealthRecord(profile);
    }
    @Test
    public void TestGetUserAge() throws Exception {
        String token = phoneLoginTest.testWithCaptcha(mockMvc,false, false);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/health/getUserAge")
                .header("authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
        MockHttpServletResponse response = result.andReturn().getResponse();
        System.out.println(response.getContentAsString());
    }
    @Test
    public void TestGetProfile() throws Exception {
        String token = phoneLoginTest.testWithCaptcha(mockMvc,false, false);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/health/getProfile")
                .header("authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
        MockHttpServletResponse response = result.andReturn().getResponse();
        System.out.println(response.getContentAsString());
    }
    @Test
    public void TestGetUserName() throws Exception {
        String token = phoneLoginTest.testWithCaptcha(mockMvc,false, false);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/health/getUserName")
                .header("authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
        MockHttpServletResponse response = result.andReturn().getResponse();
        System.out.println(response.getContentAsString());
    }
}
