package edu.tongji.backend.controller;

import cn.hutool.captcha.generator.RandomGenerator;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.netflix.ribbon.proxy.annotation.Http;
import edu.tongji.backend.clients.UserClient2;
import edu.tongji.backend.dto.*;
import edu.tongji.backend.entity.Doctor;
import edu.tongji.backend.entity.Hospital;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.mapper.HospitalMapper;
import edu.tongji.backend.service.IAccountService;
import edu.tongji.backend.service.IHospitalService;
import edu.tongji.backend.service.impl.AccountServiceImpl;
import edu.tongji.backend.util.IDCardValidator;
import edu.tongji.backend.util.Response;
import edu.tongji.backend.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.server.ExportException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.tongji.backend.util.RedisConstants.ADMIN_PERM_CODE;
import static edu.tongji.backend.util.RedisConstants.ADMIN_PERM_CODE_TIMEOUT;

@Slf4j
@RestController
@RequestMapping("/api/oa")
public class AccountController {
    @Autowired
    IAccountService accountService;
    @Autowired
    IHospitalService hospitalService;
    /**
    * <p>Description:forward function for _addHospital </p>
    * @return 401 for unauthorized
    * @see #addHospital
    *
    * */
    @PostMapping("/addHospital")
    public ResponseEntity<Response<String>> addHospital2(@RequestParam String hospital_name, @RequestParam String level,
                                                         @RequestParam String address, @RequestParam BigDecimal latitude, @RequestParam BigDecimal longitude,
                                                         @RequestParam String zipcode, @RequestParam String hospital_phone, @RequestParam String outpatient_hour,
                                                         @RequestParam String introduction){
        return addHospital(hospital_name,level,address,latitude,longitude,zipcode,hospital_phone,outpatient_hour,introduction);
    }
    /**
     * NOTE:must be accessible only to the admin
     * <p>Description:register a hospital </p>
     * @return The {@link org.springframework.http.ResponseEntity }'s status indicates that
     * <ul>
     * <li>200 for normal functioning</li>
     * <li>404/400 for client error ,usually paramter error</li>
     * </ul>
     * @throws SQLIntegrityConstraintViolationException maybe primary key contraints compromise.
     * @throws IllegalArgumentException The following 4 scenarios will trigger the exception:
     * <ol>
     * <li>the name,contact and address can't be redundant!</li>
     * <li>The zipcode must be 6-digit sequence
     * <li>the outpatient hours must adhere to the regular expression</li>
     * <li>The coord must be in the range of [(-90,-180),(90,180)]</li>
     * </ol>
     * @since 2.2.0
     * @author <a href="https://github.com/rmEleven">rmEleven</a>
     *
     *
     */
    public ResponseEntity<Response<String>> addHospital(@RequestParam String hospital_name, @RequestParam String level,
                            @RequestParam String address, @RequestParam BigDecimal latitude, @RequestParam BigDecimal longitude,
                            @RequestParam String zipcode, @RequestParam String hospital_phone, @RequestParam String outpatient_hour,
                            @RequestParam String introduction) {
        Hospital hospital = new Hospital(null, hospital_name, level, address, latitude, longitude,
                                         zipcode, hospital_phone, outpatient_hour, introduction,null);
        try {
            if (hospitalMapper.InfoRepeated(hospital_phone,hospital_name,address))
                throw new IllegalArgumentException("The hospital phone/name/address might have been used before!");
            if (!Objects.equals(level, "三甲") && !Objects.equals(level, "三乙"))
                throw new IllegalArgumentException("The level must equals to \"三甲\" or \"三乙\"");
            if (new BigDecimal("-90").compareTo(latitude) > 0 || new BigDecimal("90").compareTo(latitude) < 0)
                throw new IllegalArgumentException("The latitude has exceeded the range [-90,90]");
            if (new BigDecimal("-180").compareTo(longitude) > 0 || new BigDecimal("180").compareTo(longitude) < 0)
                throw new IllegalArgumentException("The longitude has exceeded the range [-180,180]");
            String regex = "\\b\\d{1,2}:\\d{1,2}-\\d{1,2}:\\d{1,2}\\b";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(outpatient_hour);
            if (matcher.find()) {
                log.info("值班时间"+outpatient_hour+"匹配成功！");
            } else {
                String msg="值班时间"+outpatient_hour+"匹配失败！格式必须为xx:xx-xx:xx(可以为1位数字，但必须是标准的表示间隔的形式)";
                log.error(msg);
                throw new IllegalArgumentException(msg);
            }
            if(!zipcode.matches("\\d{6}")){
                throw new IllegalArgumentException("The zipcode(CN) must contain 6 digits!");
            }
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(Response.fail(e.getMessage()), HttpStatus.OK);
        }
        try {
            Integer id = hospitalService.addHospital(hospital);
            String msg="The "+hospital_name+" has been registered successfully!";
            log.info(msg);
            return new ResponseEntity<>(Response.success(id.toString(),msg),HttpStatus.OK);
        }catch (Exception e){
            log.error(e.getMessage());
            if(e instanceof SQLIntegrityConstraintViolationException)
                return new ResponseEntity<>(Response.fail(e.getMessage()),HttpStatus.OK);
            else
                return new ResponseEntity<>(Response.fail(e.getMessage()),HttpStatus.OK);
        }
    }

    /**
     * user accessible api
     * @see #deleteHospital(Integer)
     */
    @PostMapping("/removeHospital")
    public ResponseEntity<Response<String>> deleteHospital2(@RequestParam("hospital_id") Integer hospital_id){
        return deleteHospital(hospital_id);
    }
    /**
     * <p>NOTE:must be accessible only to the admin</p>
     * <p>FIXME: the PRIMARY key needs to be automatically incremented
     * DELETE Request can't work!</p>
     * <p>TODO: authentication for the admin account</p>
     * <p>Description:unregister a hospital </p>
     * @return The {@link org.springframework.http.ResponseEntity }'s status indicates that
     * <ul>
     * <li>200 for normal functioning</li>
     * <li>404/400 for client error ,usually paramter error</li>
     * </ul>
     *

     * @throws NoSuchElementException It's OK,just means the element you want to remove doesn't exist
     * @since 2.2.0
     * @author <a href="https://github.com/rmEleven">rmEleven</a>
     *
     *
     */
    public ResponseEntity<Response<String>> deleteHospital(@RequestParam("hospital_id") Integer hospital_id) {
//        医生对医院有外键依赖
        try {
            hospitalService.deleteHospital(hospital_id);
        }catch (Exception e){
            log.error(e.getMessage());
            if(e instanceof NoSuchElementException){
                return new ResponseEntity<>(Response.success(null,e.getMessage()),HttpStatus.OK);
            }else
                return new ResponseEntity<>(Response.fail(e.getMessage()),HttpStatus.OK);
        }
        String msg="The hospital"+hospital_id+" has been removed successfully!";
        log.info(msg);
        return new ResponseEntity<>(Response.success(null,msg),HttpStatus.OK);
    }

    @GetMapping("/getAccountList")
    public ResponseEntity<Response<List<DoctorInfoDTO>>> getAccountList2(){
        return new ResponseEntity<>(getAccountList(),HttpStatus.OK);
    }
    public Response<List<DoctorInfoDTO>> getAccountList() {
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
     * @param doctor which must be a valid linux relative path,usually under an absolute path /data/www/
     * @return 200 for normal functioning,404/400 for client error ,usually paramter error
     * @throws IllegalArgumentException check the validity of IDCard,the requirements are as follows:
     * <ol>
     * <li>The length must be <b>15</b> or <b>18</b>(II)</li>
     * <li>The first 6 digits must indicate a valid regional code,such as 310101<em>(31 Shanghai,01 Municipal region,01 Huangpu District(01 usually indicates the must central district))</em></li>
     * <li>The middle 8 digits must indicate a valid birthdate that must precede today</li>
     * <li>The all 18 digits must pass the parity check,and the 18th digit is the very digit</li>
     * </ol>
     * @since 2.2.0
     * @author <a href="https://github.com/rmEleven">rmEleven</a>
     *
     *
     */
    @PostMapping("/addAccount")
    public ResponseEntity<Response<String>> addAccount2(@RequestBody DoctorDTO1 doctor)
            throws IOException, JSONException{
        return addAccount(doctor.getHospital_id(),doctor.getName(), doctor.getId_card(), doctor.getDepartment(),
                doctor.getTitle(),doctor.getPhoto_path(),
                doctor.getContact());
    }

    public ResponseEntity<Response<String>> addAccount(int hospital_id,String name, String id_card, String department,
                                                       String title, String photo_path, String contact)
            throws IOException, JSONException {
        if(name==null||name.length()==0)
            return new ResponseEntity<>(Response.fail("The doctor name can't be empty"),HttpStatus.OK);
        if(!name.matches("^[\\u4e00-\\u9fa5]{2,15}$") && !name.matches("^[a-zA-Z]{2,50}$"))
            return new ResponseEntity<>(Response.fail("The name must be pure English or Chinese"),HttpStatus.OK);
        if(id_card.length()!=18&& id_card.length()!=15)
            return new ResponseEntity<>(Response.fail("the length of ID must be 18 or 15"),HttpStatus.OK);
        String addr;
        try {
            File jsonFile;
            try {
                jsonFile= ResourceUtils.getFile("classpath:region.json");
            }catch (FileNotFoundException e){
                jsonFile=new File("/tmp/region.json");
            }
            String json = FileUtils.readFileToString(jsonFile);
            com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(json);
            com.alibaba.fastjson.JSONObject codes = jsonObject.getJSONObject("code");
            String region_candidate=id_card.substring(0,6);
            addr= String.valueOf(codes.get(region_candidate));
            //Check 0~6
            if(addr.isEmpty()||addr.equals("null")) {
                String msg="There's no region code"+region_candidate;
                log.error(msg);
                return new ResponseEntity<>(Response.fail(msg),HttpStatus.OK);
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
                    String msg = id_card.substring(6, 14) + " doesn't adhere to the format of a valid birth date";
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
            return new ResponseEntity<>(Response.fail(e.getMessage()), HttpStatus.OK);
        }
        Doctor doctor = new Doctor(null, hospital_id, id_card, department, title, photo_path);
        Boolean res=userClient2.repeatedContact(contact).getResponse();
        if (res) {  // 判断contact是否已经存在
            String msg="repeated contact is not allowed";
            log.error(msg);
            return new ResponseEntity<>(Response.fail(msg), HttpStatus.OK);
        }
        Boolean res2=accountService.repeatedIdCard(id_card);
        if (res2) {  // 判断id card是否已经存在
            String msg="repeated ID card is not allowed";
            log.error(msg);
            return new ResponseEntity<>(Response.fail(msg), HttpStatus.OK);
        }
        System.out.println(doctor.getHospitalId());
        QueryWrapper<Hospital> wrapper = new QueryWrapper<>();
        wrapper.eq("hospital_id", doctor.getHospitalId());
        Hospital result = hospitalMapper.selectOne(wrapper);
        if (result == null) {  // hospital_id不存在
            String msg="hospital does not exist";
            return new ResponseEntity<>(Response.fail(msg),HttpStatus.OK);
        }
        try {
            Integer id = accountService.addAccount(doctor, contact, addr);
            return new ResponseEntity<>(Response.success(id.toString(),"The account has been added successfully!"), HttpStatus.OK);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(Response.fail(e.getMessage()),HttpStatus.OK);
        }
    }
    @PostMapping("/deleteAccount")
    public ResponseEntity<Response<String>> deleteAccount2(@RequestParam int doctor_id){
        return deleteAccount(doctor_id);
    }
    /**
     *
     * <p>Description:remove a doctor's account along with his/her ordinary account</p>
     * @param doctor_id one with user_id of which must be a doctor ,so extra authentication is needed!
     * @return 200 for normal functioning,404/400 for client error ,usually not existing account or ghost account
     * @since 2.2.0
     * @author <a href="https://github.com/rmEleven">rmEleven</a>
     */
    public ResponseEntity<Response<String>> deleteAccount(@RequestParam int doctor_id) {
        try {
            accountService.deleteAccount(doctor_id);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(Response.fail(e.getMessage()),HttpStatus.OK);
        }
        String msg="The doctor account "+doctor_id+" has been removed";
        log.info(msg);
        return new ResponseEntity<>(Response.success(null,msg),HttpStatus.OK);
    }
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @PostMapping("/GenInviteCode")
    public ResponseEntity<Response<String>> GenerateInvitationCode(@RequestParam String hospitalId){
        if(hospitalId==null||hospitalId.length()==0||hospitalId.equals("")){
            return new ResponseEntity<>(Response.fail("The Hospital Code can't be empty"), HttpStatus.BAD_REQUEST);
        }
        if(hospitalMapper.havaAdministrator(hospitalId)!=null){
            return new ResponseEntity<>(Response.fail("The hospital has administrator already!"),HttpStatus.OK);
        }
        if(!hospitalMapper.ValidHospitalId(hospitalId)){
            return new ResponseEntity<>(Response.fail("The hospital id doesn't exist!"),HttpStatus.BAD_REQUEST);
        }
        RandomGenerator randomGenerator = new RandomGenerator(40);
        String substring = randomGenerator.generate().substring(0, 25);
        stringRedisTemplate.opsForHash().put(ADMIN_PERM_CODE,substring,hospitalId);
        //stringRedisTemplate.opsForValue().set(ADMIN_PERM_CODE + substring,hospitalId,ADMIN_PERM_CODE_TIMEOUT, TimeUnit.DAYS);
        return new ResponseEntity<>(Response.success(substring,"The Invitation Code has been generated successfully!"), HttpStatus.OK);
    }
    @PutMapping("/editAccount")
    public ResponseEntity<Response<Boolean>> editAccount(@RequestBody DoctorEditDTO doctoredit){
        String idCard=doctoredit.getIdCard();
        if(idCard.length()!=18&& idCard.length()!=15)
            return new ResponseEntity<>(Response.fail("the length of ID must be 18 or 15"),HttpStatus.OK);
        String addr;
        try {
            File jsonFile;
            try {
                jsonFile= ResourceUtils.getFile("classpath:region.json");
            }catch (FileNotFoundException e){
                jsonFile=new File("/tmp/region.json");
            }
            String json = FileUtils.readFileToString(jsonFile);
            com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(json);
            com.alibaba.fastjson.JSONObject codes = jsonObject.getJSONObject("code");
            String region_candidate=idCard.substring(0,6);
            addr= String.valueOf(codes.get(region_candidate));
            //Check 0~6
            if(addr.isEmpty()||addr.equals("null")) {
                String msg="There's no region code"+region_candidate;
                log.error(msg);
                return new ResponseEntity<>(Response.fail(msg),HttpStatus.OK);
            }
            //Check 7~14
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                LocalDate parsed = LocalDate.parse(idCard.substring(6, 14), formatter);
                if(parsed.isAfter(LocalDate.now()))
                    throw new DateTimeException("The date must reside before today!");
                Period period = Period.between(parsed, LocalDate.now());
            }catch (Exception e){

                if(!(e instanceof DateTimeException)) {
                    String msg = idCard.substring(6, 14) + " doesn't adhere to the format of a valid birth date";
                    log.error(msg);
                    throw new IllegalArgumentException(msg);
                }else{
                    throw e;
                }
            }
            boolean validate = IDCardValidator.validate(idCard);
            if(!validate){
                throw new IllegalArgumentException("The ID is invalid though the region code and birth date is valid!");
            }
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(Response.fail(e.getMessage()), HttpStatus.OK);
        }
        try {
            Doctor doctor = new Doctor(doctoredit.getDoctorId(), 0, idCard, doctoredit.getDepartment(),doctoredit.getTitle(), doctoredit.getPhotoPath());
            accountService.updateAccount(doctor);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(Response.fail(e.getMessage()), HttpStatus.OK);
        }
        try {
            User user = new User(doctoredit.getDoctorId(), null, doctoredit.getName(),doctoredit.getContact(),  null, null);
            userClient2.BrandNewUserProfile(user);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(Response.fail("Error encountered when updating the basic profile for the doctor"), HttpStatus.OK);
        }
        return new ResponseEntity<>(Response.success(true,"The profile of doctor "+doctoredit.getDoctorId()+" has been updated!"),HttpStatus.OK);
    }
    @PostMapping("/register")  //对应的api路径
    public ResponseEntity<Response<Boolean>> registerAdmin(@RequestParam String inviteCode,@RequestParam String name,
                                                           @RequestParam String contact,@RequestParam String password) throws NoSuchAlgorithmException {
        RegisterDTO info=new RegisterDTO(name,password,contact,null,null);
        String hospitalId = stringRedisTemplate.opsForHash().get(ADMIN_PERM_CODE, inviteCode).toString();
        if(hospitalId==null){
            return new ResponseEntity<>(Response.fail("邀请码已被使用或无效验证码"), HttpStatus.OK);
        }else{
            if (info.getContact() == null || info.getPassword() == null)  //如果请求中的内容不完整
            {
                return new ResponseEntity<>(Response.fail("手机号或密码为空"),HttpStatus.OK);
            }
            //The password must contain at least one digit, one lowercase, one uppercase and one special character,the length should be between 8 and 16.
            if (!info.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,16}$")) {
                return new ResponseEntity<>( Response.fail("After 2024/1/7,register rules are updated!" +
                        "The password must contain at least one digit, one lowercase, one uppercase and one special character,the length should be between 8 and 16"),
                        HttpStatus.OK);
            }
            //The phone number must be 11 digits and it's the valid phone number in China mainland.
            if (!info.getContact().matches("^[1][3,4,5,7,8][0-9]{9}$")) {
                return new ResponseEntity<>( Response.fail("After 2024/1/7,register rules are updated!"+
                        "The phone number must be 11 digits and it's the valid phone number in China mainland."),
                        HttpStatus.OK);
            }
            //The name must be 2-10 characters and it can only contain either all Chinese characters or all English characters.
            if (info.getName()==null||(!info.getName().matches("^[\\u4e00-\\u9fa5]{2,15}$") && !info.getName().matches("^[a-zA-Z]{2,50}$"))) {
                return new ResponseEntity<>(Response.fail("After 2024/1/7,register rules are updated!"+
                        "The name must be 2-10 characters and it can only contain either all Chinese characters or all English characters."),
                        HttpStatus.OK);
            }
            log.info(info.toString());

            Integer result = userClient2.registerHelper(info);  //调用接口的register函数
            if (result==-1)  //如果返回的result为false
            {
                return new ResponseEntity<>(Response.fail("管理员手机号已被注册"),HttpStatus.OK);
            }
            hospitalMapper.setAdministrator(hospitalId,result.toString());
            stringRedisTemplate.opsForHash().delete(ADMIN_PERM_CODE,inviteCode);
            return new ResponseEntity<>(Response.success(true, "管理员注册成功"),HttpStatus.OK);
        }
    }
}
