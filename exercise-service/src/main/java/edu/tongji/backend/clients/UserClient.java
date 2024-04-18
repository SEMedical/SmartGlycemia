package edu.tongji.backend.clients;

import edu.tongji.backend.entity.Profile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@FeignClient("user-service")
public interface UserClient {
    @GetMapping("/api/health/getProfile")
    Profile getByPatientId(HttpServletRequest request);
}
