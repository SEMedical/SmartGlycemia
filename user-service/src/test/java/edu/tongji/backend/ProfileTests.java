package edu.tongji.backend;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 Victor Hu
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
    @Resource
    StringRedisTemplate stringRedisTemplate;
    private PhoneLoginTest phoneLoginTest=new PhoneLoginTest();
    //在每个测试方法执行之前都初始化MockMvc对象
    @BeforeEach
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        this.session = new MockHttpSession();
    }
    @Test
    public void TestGetHealthRecord() throws Exception {
        TestGetHealthRecord("15555555555");
    }
    public void TestGetHealthRecord(String contact) throws Exception {
        String token = phoneLoginTest.testWithCaptcha(stringRedisTemplate,mockMvc,false, false,contact);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/health/health-record")
                .header("authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
        MockHttpServletResponse response = result.andReturn().getResponse();
        System.out.println(response.getContentAsString());
    }
    public void TestUpdateHealthRecord(ProfileDTO profile) throws Exception {
        String token = phoneLoginTest.testWithCaptcha(stringRedisTemplate,mockMvc,false, false);
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
    public void TestGetHealthRecordBatch() throws Exception {
        TestGetHealthRecord("15555555555");
        TestGetHealthRecord("13955555555");
        TestGetHealthRecord("15673287113");
        TestGetHealthRecord("15809922671");
        TestGetHealthRecord("13197690040");
        TestGetHealthRecord("13127851068");
    }
    @Test
    public void TestUpdateHealthRecordBatch() throws Exception {
        //Cover all complications
        ProfileDTO profile=new ProfileDTO("Female",22,"170cm","110kg","I型糖尿病","Hysteria,diabetic foot,diabetic eye,diabetic kidney," +
                "diabetic cardiovascular disease,diabetic neuropathy,diabetic skin disease," +
                "hypertension,hyperlipidemia,others",null,null);
        TestUpdateHealthRecord(profile);
        profile=new ProfileDTO("女",22,"180cm","110kg","II型糖尿病","Hysteria",null,null);
        TestUpdateHealthRecord(profile);
        profile=new ProfileDTO("男",22,"154cm","110kg","妊娠期糖尿病","Hysteria",null,null);
        TestUpdateHealthRecord(profile);
        profile=new ProfileDTO("Male",22,"170cm","110kg","II","糖尿病足," +
                "糖尿病眼,糖尿病肾,糖尿病心血管疾病,糖尿病神经病变,糖尿病皮肤病," +
                "高血压,高血脂",2019,null);
        TestUpdateHealthRecord(profile);

    }
    @Test
    public void TestGetUserAge() throws Exception {
        TestGetUserAge("15555555555");
        TestGetUserAge("15411111111");
    }

    public void TestGetUserAge(String contact) throws Exception {
        String token = phoneLoginTest.testWithCaptcha(stringRedisTemplate,mockMvc,false, false,contact);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/health/getUserAge")
                .header("authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
        MockHttpServletResponse response = result.andReturn().getResponse();
        System.out.println(response.getContentAsString());
    }
    @Test
    public void TestGetProfileBatch() throws Exception{
        TestGetProfile("15555555555");
        TestGetProfile("13845115878", status().isIAmATeapot());
        TestGetProfile("13955555555");
        TestGetProfile("18913599653", status().isIAmATeapot());
        TestGetProfile("15673287113");
        TestGetProfile("15809922671");
        TestGetProfile("15565644489",status().isIAmATeapot());
        TestGetProfile("13197690040");
        TestGetProfile("13127851068");
    }
    public void TestGetProfile(String contact) throws Exception {
        TestGetProfile(contact,status().isOk());
    }

    public void TestGetProfile(String contact, ResultMatcher matcher) throws Exception {
        String token = phoneLoginTest.testWithCaptcha(stringRedisTemplate,mockMvc,false, false,contact);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/health/getProfile")
                .header("authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(matcher);
        MockHttpServletResponse response = result.andReturn().getResponse();
        System.out.println(response.getContentAsString());
    }
    @Test
    public void TestGetUserName() throws Exception {
        TestGetUserName("15555555555");
        TestGetUserName("15411111111");//FIXME: suffer when name is null
    }
    @Test
    public void TestMaxUserIdBatch() throws Exception {
        TestMaxUserId();
    }
    public void TestMaxUserId() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/login/getMaxUserId")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
        MockHttpServletResponse response = result.andReturn().getResponse();
        System.out.println(response.getContentAsString());
    }
    public void TestGetUserName(String contact) throws Exception {
        String token = phoneLoginTest.testWithCaptcha(stringRedisTemplate,mockMvc,false, false,contact);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/health/getUserName")
                .header("authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
        MockHttpServletResponse response = result.andReturn().getResponse();
        System.out.println(response.getContentAsString());
    }
}
