package edu.tongji.backend.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.netflix.ribbon.proxy.annotation.Http;
import edu.tongji.backend.clients.UserClient2;
import edu.tongji.backend.dto.DoctorInfoDTO;
import edu.tongji.backend.entity.Doctor;
import edu.tongji.backend.entity.Hospital;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.mapper.HospitalMapper;
import edu.tongji.backend.service.IAccountService;
import edu.tongji.backend.service.IHospitalService;
import edu.tongji.backend.util.IDCardValidator;
import edu.tongji.backend.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.server.ExportException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestController  //用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/api/oa")  //对应的api路径
public class AccountController {
    @Autowired
    IAccountService accountService;
    @Autowired
    IHospitalService hospitalService;
    /**
     * NOTE:must be accessible only to the admin
     * <p>Description:register a hospital </p>
     * @return 200 for normal functioning,404/400 for client error ,usually paramter error
     * @throws SQLIntegrityConstraintViolationException maybe primary key contraints compromise.
     * FIXME: the PRIMARY key needs to be automatically incremented
     * @since 2.2.0
     * @author <a href="https://github.com/rmEleven">rmEleven</a>
     *
     *
     */
    @PostMapping("/addHospital")
    public ResponseEntity<Response<String>> addHospital(@RequestParam int hospital_id, @RequestParam String hospital_name, @RequestParam String level,
                            @RequestParam String address, @RequestParam BigDecimal latitude, @RequestParam BigDecimal longitude,
                            @RequestParam String zipcode, @RequestParam String hospital_phone, @RequestParam String outpatient_hour,
                            @RequestParam String introduction) {
        System.out.println("添加医院");
        Hospital hospital = new Hospital(hospital_id, hospital_name, level, address, latitude, longitude,
                                         zipcode, hospital_phone, outpatient_hour, introduction);
        try {
            hospitalService.addHospital(hospital);
        }catch (Exception e){
            log.error(e.getMessage());
            if(e instanceof SQLIntegrityConstraintViolationException)
                return new ResponseEntity<>(Response.fail(e.getMessage()),HttpStatus.CONFLICT);
            else
                return new ResponseEntity<>(Response.fail(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
        String msg="The "+hospital_name+" has been registered successfully!";
        log.info(msg);
        return new ResponseEntity<>(Response.success(null,msg),HttpStatus.NOT_FOUND);
    }
    /**
     * NOTE:must be accessible only to the admin
     * <p>Description:unregister a hospital </p>
     * @return 200 for normal functioning,404/400 for client error ,usually the hospital doesn't exist
     * FIXME: the PRIMARY key needs to be automatically incremented
     * DELETE Request can't work!
     * TODO: authentication for the admin account
     * @throws NoSuchElementException It's OK,just means the element you want to remove doesn't exist
     * @since 2.2.0
     * @author <a href="https://github.com/rmEleven">rmEleven</a>
     *
     *
     */
    @PostMapping("/removeHospital")
    public ResponseEntity<Response<String>> deleteHospital(@RequestParam("hospital_id") Integer hospital_id) {
//        医生对医院有外键依赖
        System.out.println("删除医院");
        try {
            hospitalService.deleteHospital(hospital_id);
        }catch (Exception e){
            log.error(e.getMessage());
            if(e instanceof NoSuchElementException){
                return new ResponseEntity<>(Response.success(null,e.getMessage()),HttpStatus.NOT_FOUND);
            }else
                return new ResponseEntity<>(Response.fail(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
        String msg="The hospital"+hospital_id+" has been removed successfully!";
        log.info(msg);
        return new ResponseEntity<>(Response.success(null,msg),HttpStatus.OK);
    }

    @GetMapping("/getAccountList")
    public Response<List<DoctorInfoDTO>> getAccountList() {
        System.out.println("查看账号");
        List<DoctorInfoDTO> accountList=new ArrayList<>();
        try {
            accountList = accountService.getAccountList();
        }catch (Exception e){
            log.error(e.getMessage());
            return Response.fail(e.getMessage());
        }
        String msg="return list success";
        log.info(msg);
        return Response.success(accountList,msg);
    }
    @Autowired
    UserClient2 userClient2;
    @Autowired
    HospitalMapper hospitalMapper;
    /**
     * TODO:automatically incremental doctor id
     * <p>Description:the addAccount Method is used for the addition of the account for a doctor by adminisrator</p>
     * @param photo_path which must be a valid linux relative path,usually under an absolute path /data/www/
     * @return 200 for normal functioning,404/400 for client error ,usually paramter error
     * @throws IllegalArgumentException check the validity of IDCard,the requirements are as follows:
     * 1. The length must be <b>15</b> or <b>18</b>(II)
     * 2. The first 6 digits must indicate a valid regional code,such as 310101<em>(31 Shanghai,01 Municipal region,01 Huangpu District(01 usually indicates the must central district))</em>
     * 3. The middle 8 digits must indicate a valid birthdate that must precede today
     * 4. The all 18 digits must pass the parity check,and the 18th digit is the very digit
     * @since 2.2.0
     * @author <a href="https://github.com/rmEleven">rmEleven</a>
     *
     *
     */
    @PostMapping("/addAccount")
    public ResponseEntity<Response<String>> addAccount(@RequestParam int doctor_id, @RequestParam int hospital_id, @RequestParam String id_card,
                                                    @RequestParam String department, @RequestParam String title, @RequestParam String photo_path, @RequestParam String contact)
            throws IOException, JSONException {
        if(id_card.length()!=18&& id_card.length()!=15)
            return new ResponseEntity<>(Response.fail("the length of ID must be 18 or 15"),HttpStatus.BAD_REQUEST);
        String addr;
        try {
            File jsonFile = ResourceUtils.getFile("classpath:region.json");
            String json = FileUtils.readFileToString(jsonFile);
            com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(json);
            com.alibaba.fastjson.JSONObject codes = jsonObject.getJSONObject("code");
            String region_candidate=id_card.substring(0,6);
            addr= String.valueOf(codes.get(region_candidate));
            //Check 0~6
            if(addr.isEmpty()||addr.equals("null")) {
                String msg="There's no region code"+region_candidate;
                log.error(msg);
                return new ResponseEntity<>(Response.fail(msg),HttpStatus.BAD_REQUEST);
            }
            //Check 7~14
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                LocalDate parsed = LocalDate.parse(id_card.substring(6, 14), formatter);
                if(parsed.isAfter(LocalDate.now()))
                    throw new DateTimeException("The date must reside before today!");
                Period period = Period.between(parsed, LocalDate.now());
            }catch (Exception e){

                if(!(e instanceof DateTimeException)) {
                    String msg = id_card.substring(7, 14) + " doesn't adhere to the format of a valid birth date";
                    log.error(msg);
                    throw new IllegalArgumentException(msg);
                }else{
                    throw e;
                }
            }
            boolean validate = IDCardValidator.validate(id_card);
            if(!validate){
                throw new IllegalArgumentException("The ID is invalid though the region code and birth date is valid!");
            }
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(Response.fail(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        System.out.println("添加账号");
        Doctor doctor = new Doctor(doctor_id, hospital_id, id_card, department, title, photo_path);
        System.out.println("判断contact是否已经存在");
        Boolean res=userClient2.repeatedContact(contact).getResponse();
        if (res) {  // 判断contact是否已经存在
            String msg="repeated contact is not allowed";
            log.error(msg);
            return new ResponseEntity<>(Response.fail(msg), HttpStatus.BAD_REQUEST);
        }
        System.out.println(doctor.getHospitalId());
        QueryWrapper<Hospital> wrapper = new QueryWrapper<>();
        wrapper.eq("hospital_id", doctor.getHospitalId());
        Hospital result = hospitalMapper.selectOne(wrapper);
        if (result == null) {  // hospital_id不存在
            String msg="hospital does not exist";
            return new ResponseEntity<>(Response.fail(msg),HttpStatus.NOT_FOUND);
        }
        try {
            accountService.addAccount(doctor, contact,addr);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(Response.fail(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(Response.success(null,"The account has been added successfully!"), HttpStatus.OK);
    }
    /**
     *
     * <p>Description:remove a doctor's account along with his/her ordinary account</p>
     * @param doctor_id one with user_id of which must be a doctor ,so extra authentication is needed!
     * @return 200 for normal functioning,404/400 for client error ,usually not existing account or ghost account
     * @since 2.2.0
     * @author <a href="https://github.com/rmEleven">rmEleven</a>
     */
    @PostMapping("/deleteAccount")
    public ResponseEntity<Response<String>> deleteAccount(@RequestParam int doctor_id) {
        System.out.println("删除账号");
        try {
            accountService.deleteAccount(doctor_id);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(Response.fail(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
        String msg="The doctor account "+doctor_id+" has been removed";
        log.info(msg);
        return new ResponseEntity<>(Response.success(null,msg),HttpStatus.OK);
    }
}
