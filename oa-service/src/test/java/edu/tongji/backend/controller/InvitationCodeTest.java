package edu.tongji.backend.controller;

import com.alibaba.fastjson.JSONObject;
import edu.tongji.backend.dto.DoctorEditDTO;
import edu.tongji.backend.service.impl.AccountServiceImpl;
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
import java.util.HashMap;
import java.util.Map;

import static edu.tongji.backend.util.RedisConstants.LOGIN_TOKEN_KEY;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Slf4j
public class InvitationCodeTest {
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
    void editDoctorTestSuites() throws Exception {
        editDoctorTest(121,"John","130101200201017552","Lung","Dean","15544471234","/tmp/4.png","Free");
        editDoctorTest(121,"John","130101200201017553","Lung","Dean","15544471234","/tmp/4.png","Free");
        editDoctorTest(121,"John","13010120020101","Lung","Dean","15544471234","/tmp/4.png","Free");
        editDoctorTest(121,"John","130101200201017552","Lung","Dean","15544471234","/tmp/4.png","Free");
        editDoctorTest(121,"John","130101200201017552","Lung","Dean","15544471","/tmp/4.png","Free");
        editDoctorTest(121,"John","130001200201017552","Lung","Dean","15544471234","/tmp/4.png","Free");
        editDoctorTest(121,"John","130101100201017552","Lung","Dean","15544471234","/tmp/4.png","Free");
    }
    @Test
    void GenerateInvitationCodeTestSuites() throws Exception {
        for(int i=0;i<10;i++)
            GenerateInvitationCodeTest(Integer.valueOf(i));
    }
    void GenerateInvitationCodeTest(Integer hospitalId) throws Exception {
        String token="srtbm,glb15vx15vz0cs15v15v1";
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
        Map<String,String> maps=new HashMap<>();
        maps.put("name","momo");
        maps.put("id","0");
        maps.put("role","admin");
        stringRedisTemplate.opsForHash().putAll(LOGIN_TOKEN_KEY+token,maps);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/oa/GenInviteCode")
                .param("hospitalId",hospitalId.toString())
                .contentType("application/json")
                .header("Authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
    }
    //Only wrong test so far
    @Test
    void registerAdminTestSuites() throws Exception {
        registerAdminTest("svd5svvsvrdv","momvrvvo","15544448888","vdscszS1!m,");
    }
    void registerAdminTest(String inviteCode,String name,String contact,String password) throws Exception {
        String token="srtbm,glb15vx15vz0cs15v15v1";
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
        Map<String,String> maps=new HashMap<>();
        maps.put("name","momo");
        maps.put("id","0");
        maps.put("role","admin");
        stringRedisTemplate.opsForHash().putAll(LOGIN_TOKEN_KEY+token,maps);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/oa/register")
                .param("inviteCode",inviteCode)
                .param("name",name)
                .param("contact",contact)
                .param("password",password)
                .header("Authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
    }
    void editDoctorTest(DoctorEditDTO doctor) throws Exception {
        String token="srtbm,glb15vx15vz0cs15v15v1";
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
        Map<String,String> maps=new HashMap<>();
        maps.put("name","momo");
        maps.put("id","0");
        maps.put("role","admin");
        stringRedisTemplate.opsForHash().putAll(LOGIN_TOKEN_KEY+token,maps);
        String jsonString = JSONObject.toJSONString(doctor);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/oa/editAccount")
                .content(jsonString)
                .contentType("application/json")
                .header("Authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
    }
    void editDoctorTest(Integer doctorId,String name,String idCard,String department,String title,String contact,String photoPath,String state) throws Exception {
        DoctorEditDTO doctor=new DoctorEditDTO(doctorId,name,idCard,department,title,contact,photoPath,state);
        editDoctorTest(doctor);
    }
    @Test
    void editAdminInfoTestSuites() throws Exception {
        editAdminTest("Vincent","0216958110");
        editAdminTest("Vincent","0216958121");
        editAdminTest("Vincent","15555555557");
        editAdminTest("Vincent","0216958110");
        editAdminTest("Vincentvdbdfnnbvfnbvdfbvfvsvdfsv","0216958120");
    }
    void editAdminTest(String name,String contact) throws Exception {
        String token="srtbm,glb15vx15vz0cs15v15v1";
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
        Map<String,String> maps=new HashMap<>();
        maps.put("name","momo");
        maps.put("id","0");
        maps.put("role","admin");
        stringRedisTemplate.opsForHash().putAll(LOGIN_TOKEN_KEY+token,maps);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/oa/editAdminInfo")
                .param("name",name)
                .param("contact",contact)
                .header("Authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
    }
}
