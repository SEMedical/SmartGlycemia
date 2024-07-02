package edu.tongji.backend.clients;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 Victor Hu,rmEleven
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */





import edu.tongji.backend.config.FeignConfig;
import edu.tongji.backend.dto.AdminDTO;
import edu.tongji.backend.dto.RegisterDTO;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.util.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@FeignClient(name="user-service",configuration = FeignConfig.class)
public interface UserClient2 {
    @PostMapping(value="/api/register/addUser")
    void addUser(User user);
    @PostMapping (value="/api/register/rmUser")
    void rmUser(@RequestParam("userId") Integer userId);
    @GetMapping(value="/api/login/repeatedContact")
    Response<Boolean> repeatedContact(@RequestParam("contact") String contact);
    @GetMapping(value="/api/login/getMaxUserId")
    Integer getMaxUserId();
    @PostMapping (value="/api/register/registerHelper")
    Integer registerHelper(@RequestBody RegisterDTO registerDTO) throws NoSuchAlgorithmException;
    @PostMapping(value="/api/register/refresh")
    ResponseEntity<Response<Boolean>> BrandNewUserProfile(@RequestBody User user);
    @GetMapping(value="/api/login/getContactForAdmin")
    String getContactForAdmin(@RequestParam("userId") String userId);
    @PostMapping("/api/login/updateAdminInfo")
    Boolean updateAdminInfo(@RequestBody AdminDTO admin);
}
