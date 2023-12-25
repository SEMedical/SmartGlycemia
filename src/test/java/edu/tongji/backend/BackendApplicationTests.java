package edu.tongji.backend;

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
}
