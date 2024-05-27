package edu.tongji.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.dto.LoginDTO;
import edu.tongji.backend.dto.LoginFormDTO;
import edu.tongji.backend.dto.Result;
import edu.tongji.backend.dto.UserDTO;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.util.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;

public interface IUserService extends IService<User> {
    public Result sendCode(String contact, HttpSession session);
    public ResponseEntity<Response<LoginDTO>> loginByPhone(@RequestBody LoginFormDTO loginForm, HttpSession session);
    LoginDTO login(String contact, String password) throws NoSuchAlgorithmException;
    Integer register(String name, String password, String contact, String gender, Integer age) throws NoSuchAlgorithmException;
    Integer register(String name, String password, String contact);
    Integer getUserId(String contact);

    Result sign(UserDTO user);
    //Consecutive sign
    Result signCount(UserDTO user);

    void addUser(User user);

    void rmUser(Integer userId);
}
