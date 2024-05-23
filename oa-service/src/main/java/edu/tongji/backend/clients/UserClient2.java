package edu.tongji.backend.clients;

import edu.tongji.backend.config.FeignConfig;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.util.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="user-service",configuration = FeignConfig.class)
public interface UserClient2 {
    @PostMapping(value="/api/register/addUser")
    void addUser(User user);
    @PostMapping (value="/api/register/rmUser")
    void rmUser(@RequestParam("userId") Integer userId);
    @GetMapping(value="/api/login/repeatedContact")
    Response<Boolean> repeatedContact(@RequestParam("contact") String contact);
}
