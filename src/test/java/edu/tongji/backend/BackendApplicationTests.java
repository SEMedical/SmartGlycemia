package edu.tongji.backend;

import edu.tongji.backend.controller.GlycemiaController;
import edu.tongji.backend.exception.GlycemiaException;
import edu.tongji.backend.mapper.ExerciseMapper;
import edu.tongji.backend.mapper.GlycemiaMapper;
import edu.tongji.backend.mapper.ProfileMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.mapper.UserMapper;
import edu.tongji.backend.service.impl.ExerciseServiceImpl;
import edu.tongji.backend.service.impl.GlycemiaServiceImpl;
import edu.tongji.backend.util.Jwt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @Autowired
    ExerciseServiceImpl exerciseService;
    @Autowired
    ExerciseMapper exerciseMapper;
    GlycemiaServiceImpl glycemiaService;
    @Test
    void contextLoads() {
        glycemiaService.showGlycemiaHistoryDiagram("Week", "2", LocalDate.of(2023, 12, 27));
    }
    @Test
    void getLatestGlycemia(){
        //assertThrows(GlycemiaException.class, () -> {
        //    glycemiaService.getLatestGlycemia("1");
        //});
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
//            glycemiaController.LookupChart("key","History", "2", "2023-12-27");
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
    void testExerciseInsertion(){
        
    }
    @Test
    void testDailyDiagram(){
        System.out.println(glycemiaService.showDailyGlycemiaDiagram("1", LocalDate.of(2024, 1, 2)));
    }
}
