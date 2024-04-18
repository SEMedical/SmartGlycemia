package edu.tongji.backend.clients;

import edu.tongji.backend.util.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@FeignClient("user-service")
public interface UserClient {
    @GetMapping("/api/health/getUserAge")
    Response<Integer> getUserAge(HttpServletRequest request);
}
