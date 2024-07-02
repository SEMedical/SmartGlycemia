package edu.tongji.backend.service;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 Victor Hu,UltraTempest10
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





import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.dto.LoginDTO;
import edu.tongji.backend.dto.LoginFormDTO;
import edu.tongji.backend.dto.Result;
import edu.tongji.backend.dto.UserDTO;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.util.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;

public interface IUserService extends IService<User> {
    public ResponseEntity<Result> sendCode(String contact, HttpSession session);
    public ResponseEntity<Response<LoginDTO>> loginByPhone(@RequestBody LoginFormDTO loginForm, HttpSession session);
    LoginDTO login(String contact, String password) throws NoSuchAlgorithmException;
    @Transactional
    Result createUserWithPhone(String contact, QueryWrapper<User> wrapper);
    Integer register(String name, String password, String contact, String gender, Integer age) throws NoSuchAlgorithmException;
    void initContactBF();
    void rmContactBF();
    ResponseEntity<Response<Integer>> sign(UserDTO user);
    //Consecutive sign
    ResponseEntity<Response<Integer>> signCount(UserDTO user);

    Boolean unregister(Integer userId);

    void addUser(User user) ;

    void rmUser(Integer userId);

    Integer registerAdmin(String name, String password, String contact, String gender, Integer age) throws NoSuchAlgorithmException;

    String getContact(String userId);
    Boolean updateAdminInfo(String adminId,String name,String contact);

    void logout(String authorization,String userId);

    void updateImage(String userId, String savePath);

    Boolean validContact(String contact);

    Boolean repeatContact(String contact);
}
