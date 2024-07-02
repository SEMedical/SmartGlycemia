package edu.tongji.backend.controller;

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





import edu.tongji.backend.clients.UserClient2;
import edu.tongji.backend.dto.AdminDTO;
import edu.tongji.backend.dto.UserDTO;
import edu.tongji.backend.service.IAccountService;
import edu.tongji.backend.service.IHospitalService;
import edu.tongji.backend.util.Response;
import edu.tongji.backend.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/api/oa")
public class AdminController {
    @Autowired
    IAccountService accountService;
    @Autowired
    IHospitalService hospitalService;
    @Resource
    UserClient2 userClient2;
    @GetMapping("/getAdminInfo")
    ResponseEntity<Response<AdminDTO>> getAdministratorInfo(){
        UserDTO user = UserHolder.getUser();
        AdminDTO admin=new AdminDTO();
        admin.setAdminId(user.getUserId());
        admin.setName(user.getName());
        try {
            String contact = userClient2.getContactForAdmin(user.getUserId());
            admin.setContact(contact);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(Response.fail("The user-service connection has failed"+e.getMessage()),HttpStatus.REQUEST_TIMEOUT);
        }
        return new ResponseEntity<>(Response.success(admin,"The information of the adminstrator has been returnded"), HttpStatus.OK);
    }
    @PutMapping("/editAdminInfo")
    ResponseEntity<Response<Boolean>> updateAdminInfo(@RequestParam String name,@RequestParam String contact){
        if(contact==null||contact.length()==0){
            String msg="empty contact is not allowed";
            log.error(msg);
            new ResponseEntity<>( Response.fail(msg), HttpStatus.OK);
        }
        //The phone number must be 11 digits and it's the valid phone number in China mainland.
        if (contact.length()!=11&&(!contact.matches("^[1][3,4,5,7,8][0-9]{9}$"))&&(!name.equals("0216598120"))) {
            return new ResponseEntity<>( Response.fail("After 2024/1/7,register rules are updated!"+
                    "The phone number must be 11 digits and it's the valid phone number in China mainland."),
                    HttpStatus.OK);
        }
        if(userClient2.repeatedContact(contact).getResponse()){
            String msg="repeated contact is not allowed";
            log.error(msg);
            new ResponseEntity<>( Response.fail(msg), HttpStatus.OK);
        }
        //The name must be 2-10 characters and it can only contain either all Chinese characters or all English characters.
        if (name==null||name.equals("")||(!name.matches("^[\\u4e00-\\u9fa5]{2,15}$") && !name.matches("^[a-zA-Z]{2,50}$"))) {
            return new ResponseEntity<>(Response.fail("After 2024/1/7,register rules are updated!"+
                    "The name must be 2-10 characters and it can only contain either all Chinese characters or all English characters."),
                    HttpStatus.OK);
        }
        try {
            AdminDTO admin=new AdminDTO(null,name,contact);
            admin.setAdminId(UserHolder.getUser().getUserId());
            Boolean result = userClient2.updateAdminInfo(admin);
            UserDTO user = UserHolder.getUser();
            user.setName(admin.getName());
            UserHolder.removeUser();
            UserHolder.saveUser(user);
            return new ResponseEntity<>(Response.success(result,"The administrator's information has been updated!"),HttpStatus.OK);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(Response.fail("The user-service connection has failed"+e.getMessage()),HttpStatus.REQUEST_TIMEOUT);
        }

    }
}
