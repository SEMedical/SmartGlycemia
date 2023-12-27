package edu.tongji.backend;

import edu.tongji.backend.controller.GlycemiaController;
import edu.tongji.backend.mapper.GlycemiaMapper;
import edu.tongji.backend.mapper.ProfileMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendApplicationTests {
    @Autowired
    UserMapper userMapper;
    @Autowired
    ProfileMapper profileMapper;
    @Autowired
    GlycemiaMapper glycemiaMapper;
    @Autowired
    GlycemiaController glycemiaController;
    @Test
    void contextLoads() {
    }
    @Test
    void testSelect(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.select("user_id", "role")
                .eq("user_id", 1)
                .eq("password", "your_password2");
        System.out.println(userMapper.selectOne(wrapper));
    }
    @Test
    void testSelectGlycemia(){
        try {
            glycemiaController.LookupChart("Realtime", "2", "2023-12-27");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
