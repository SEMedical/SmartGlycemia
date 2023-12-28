package edu.tongji.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.dto.LoginDTO;
import edu.tongji.backend.entity.User;

public interface IUserService extends IService<User> {
    LoginDTO login(String contact, String password);
    Integer register(String name, String password, String contact, String gender, Integer age);
    Integer register(String name, String password, String contact);
}
