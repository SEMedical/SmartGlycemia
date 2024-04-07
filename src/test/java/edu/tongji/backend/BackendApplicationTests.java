package edu.tongji.backend;


import edu.tongji.backend.controller.GlycemiaController;
import edu.tongji.backend.controller.LoginController;
import edu.tongji.backend.controller.RegisterController;
import edu.tongji.backend.dto.RegisterDTO;
import edu.tongji.backend.exception.GlycemiaException;
import edu.tongji.backend.mapper.ExerciseMapper;
import edu.tongji.backend.mapper.GlycemiaMapper;
import edu.tongji.backend.mapper.ProfileMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.mapper.UserMapper;
import edu.tongji.backend.service.impl.ExerciseServiceImpl;
import edu.tongji.backend.service.impl.GlycemiaServiceImpl;
import edu.tongji.backend.service.impl.RunningServiceImpl;
import edu.tongji.backend.service.impl.UserServiceImpl;
import edu.tongji.backend.util.Jwt;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
@Slf4j
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
    UserServiceImpl userService;
    @Autowired
    ExerciseMapper exerciseMapper;
    @Autowired
    GlycemiaServiceImpl glycemiaService;
    @Autowired
    RegisterController register;
    public static final Logger LOGGER= LoggerFactory.getLogger(BackendApplicationTests.class);
    //一个用于测试实时获取运动数据的测试用例
    @Test
    void testExercise2() throws Exception {
        exerciseService.Init_exerciseRunning();
        System.out.println("Start test");
        Integer i1 = exerciseService.addExercise("1", 121.20947, 31.282196);
        if(i1==-1)return;
        List<Double> longis=new ArrayList<>();
        List<Double> latis=new ArrayList<>();
        //From Cao'an Highway No.4800 to the Lookup to the Sky
        longis.add(121.2094711);
        latis.add(31.282196);

        longis.add(121.212404);
        latis.add(31.282595);
        //31.2827989,121.2076651
        longis.add(121.2076651);
        latis.add(31.2827989);
        //31.283511, 121.212851
        longis.add(121.212851);
        latis.add(31.283511);
        //31.284311, 121.213488
        longis.add(121.213488);
        latis.add(31.284311);
        for (int i = 0; i < 5; i++) {
            Thread.sleep(1000);//1s<10s
            exerciseService.getRealTimeSport("1",longis.get(i),latis.get(i));
        }
        exerciseService.finishExercise("1");
        log.info("test");
        System.out.println("End test");
    }
    @Test
    void showGlycemiaHistoryDiagramBatch() {//Only w/ Redis:11s891(66.8%)  w/Redis&Bloom 4s103(65.5%,88.5%) None:35s844
        glycemiaService.Init_GlycemiaHistoryDiagram();
        for(int i=0;i<100;i++)
            glycemiaService.showGlycemiaHistoryDiagram("Week", "2", LocalDate.of(2023, 12, 27));
    }
    @Test
    void showGlycemiaDiagram() {//Only w/ Redis:8s964(-115.7%?)  w/Redis&Bloom 1s367(84.8%,67.1%) None:4s155
        glycemiaService.Init_GlycemiaDiagram();
        for(int i=0;i<5;i++)
            glycemiaService.showGlycemiaDiagram("History", "2", LocalDate.of(2023, 12, 28));
    }
    @Test
    void showDailyGlycemiaDiagram() {//Only w/ Redis:9s842(-115.7%?)  w/Redis&Bloom 2s338(84.8%,67.1%) None:5s425
        glycemiaService.Init_DailyGlycemiaDiagram();
        for(int i=0;i<5;i++)
            glycemiaService.showDailyGlycemiaDiagram( "1", LocalDate.of(2023, 12, 27));
    }
    @Test
    void testTx1(){
        userService.register("Alice","femmves","12345678912","Female",21);
    }
    @Test
    void register(){
        register.registerPatient(new RegisterDTO("Bob","123456Aa,","16055555554","Male",21));
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
    void testExerciseTx() throws InterruptedException {
        System.out.println("Start test");
        exerciseService.addExercise("1",1.0,1.0);
        //Sleep
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        exerciseService.finishExercise("1");
        System.out.println("End test");
    }
    @Test
    void testExerciseInsertion() throws Exception {
        exerciseService.Init_exerciseRunning();
        //assert that the time is now
        Integer exercise_id = exerciseService.addExercise("1",1.0,1.0);
        if(exercise_id==-1) {
            System.out.println("Collision");
            return;
        }
        //if(exerciseMapper.selectById(exercise_id).getCategory().equalsIgnoreCase("walking"))
        if(exerciseMapper.selectById(exercise_id).getCategory().equalsIgnoreCase("yoga"))
            return;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = exerciseService.getRealTimeSport("1",1.0,1.0).getTime();
        Pattern pattern = Pattern.compile("\\d+"); // 匹配一个或多个数字
        Matcher matcher = pattern.matcher(time);

        if (matcher.find()) {
            // 找到匹配的数字部分
            String numericPart = matcher.group();

            // 将提取的数字部分转换为整数
            int numericValue = Integer.parseInt(numericPart);

            // 断言时间是否小于10分钟
            assertTrue(numericValue < 10, "The time is not now");
        } else {
            // 没有找到匹配的数字部分，可能需要进行错误处理
            System.err.println("No numeric part found in the time string");
        }
    }
    @Test
    void testDailyDiagram(){
        System.out.println(glycemiaService.showDailyGlycemiaDiagram("1", LocalDate.of(2024, 1, 2)));
    }
}
