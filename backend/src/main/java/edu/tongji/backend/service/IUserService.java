package edu.tongji.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.entity.user;

public interface IUserService extends IService<user> {
    boolean login(Long id, String password);
    Integer register(String name, String password,Integer age,String contact);
}
