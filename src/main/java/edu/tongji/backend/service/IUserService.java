package edu.tongji.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.dto.LoginDTO;
import edu.tongji.backend.dto.LoginFormDTO;
import edu.tongji.backend.dto.Result;
import edu.tongji.backend.dto.UserDTO;
import edu.tongji.backend.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;

public interface IUserService extends IService<User> {
    public Result sendCode(String contact, HttpSession session);
    public Result loginByPhone(@RequestBody LoginFormDTO loginForm, HttpSession session);
    LoginDTO login(String contact, String password);
    Integer register(String name, String password, String contact, String gender, Integer age);
    Integer register(String name, String password, String contact);
    Integer getUserId(String contact);

    Result sign(UserDTO user);
    //Consecutive sign
    Result signCount(UserDTO user);
}
