package edu.tongji.backend;

import edu.tongji.backend.controller.GlycemiaController;
import edu.tongji.backend.exception.GlycemiaException;
import edu.tongji.backend.mapper.GlycemiaMapper;
import edu.tongji.backend.mapper.ProfileMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.mapper.UserMapper;
import edu.tongji.backend.util.Jwt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
        Jwt.generate(Map.of("userId", "1", "userPermission", "patient"));
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
        System.out.println("Start test");

        System.out.println("End test");
//        assertThrows(GlycemiaException.class, () -> {
//            //glycemiaController.LookupChart("key","History", "2", "2023-12-27");
//
//        });
    }
    @Test
    void testSelectGlycemiaRecord(){
        System.out.println("Start test");
//        assertThrows(GlycemiaException.class, () -> {
//            //glycemiaController.LookupChartRecord("Week", "2", "2023-12-27");
//        });
        System.out.println("End test");
    }
    @Test
    void testSelectGlycemiaRecord(){
        System.out.println("Start test");
        assertThrows(GlycemiaException.class, () -> {
            glycemiaController.LookupChartRecord("Week", "2", "2023-12-27");
        });
        System.out.println("End test");
    }
}
