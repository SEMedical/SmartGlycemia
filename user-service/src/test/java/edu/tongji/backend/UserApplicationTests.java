package edu.tongji.backend;

import edu.tongji.backend.controller.RegisterController;
import edu.tongji.backend.dto.RegisterDTO;
import edu.tongji.backend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserApplicationTests {
    @Autowired
    UserServiceImpl userService;
    @Autowired
    RegisterController register;
    //一个用于测试实时获取运动数据的测试用例
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
}
