package edu.tongji.backend.controller;

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
        //The phone number must be 11 digits and it's the valid phone number in China mainland.
        if (!contact.matches("^[1][3,4,5,7,8][0-9]{9}$")) {
            return new ResponseEntity<>( Response.fail("After 2024/1/7,register rules are updated!"+
                    "The phone number must be 11 digits and it's the valid phone number in China mainland."),
                    HttpStatus.OK);
        }
        //The name must be 2-10 characters and it can only contain either all Chinese characters or all English characters.
        if (name==null||(name.equals("0216598120")&&(!name.matches("^[\\u4e00-\\u9fa5]{2,15}$") && !name.matches("^[a-zA-Z]{2,50}$")))) {
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
