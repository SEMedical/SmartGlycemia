package edu.tongji.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.entity.User;

public interface IUserService extends IService<User> {
    boolean login(Integer id, String password);
    Integer register(String name, String password,Integer age,String contact);
}
