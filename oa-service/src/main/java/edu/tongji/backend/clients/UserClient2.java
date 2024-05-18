package edu.tongji.backend.clients;

import edu.tongji.backend.config.FeignConfig;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.util.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="user-service",configuration = FeignConfig.class)
public interface UserClient2 {
    @PostMapping(value="/api/register/addUser")
    void addUser(User user);
    @DeleteMapping (value="/api/register/removeUser")
    void removeUser(Integer userId);
}
