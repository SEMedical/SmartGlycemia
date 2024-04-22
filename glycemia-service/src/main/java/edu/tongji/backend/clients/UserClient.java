package edu.tongji.backend.clients;

import edu.tongji.backend.config.FeignConfig;
import edu.tongji.backend.util.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@FeignClient(name="user-service",configuration = FeignConfig.class)
public interface UserClient {
    @GetMapping(value="/api/health/getUserAge")
    Response<Integer> getUserAge();
}
