package edu.tongji.backend.controller;

import com.netflix.client.ClientException;
import edu.tongji.backend.dto.DoctorInfoDTO;
import edu.tongji.backend.entity.Hospital;
import edu.tongji.backend.service.IAccountService;
import edu.tongji.backend.service.IHospitalService;
import edu.tongji.backend.util.Response;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountControllerTest {
    @Autowired
    AccountController accountController;

    @Test
    void testAddHospitalRepeatedHospitalName() {
        String hospitalName = "瑞金医院";
        String level = "三甲";
        String address = "嘉定区曹安公路1";
        BigDecimal latitude = new BigDecimal("30");
        BigDecimal longitude = new BigDecimal("120");
        String zipcode = "200062";
        String hospitalPhone = "120001";
        String outpatientHours = "8:00-17:00";
        String introduction = "测试重复的hospitalName";

        ResponseEntity<Response<String>> response = accountController.addHospital(hospitalName, level, address,
                latitude, longitude, zipcode,
                hospitalPhone, outpatientHours, introduction);

        Assertions.assertEquals("The hospital phone/name/address might have been used before!",
                Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testAddHospitalInvalidHospitalLevel() {
        String hospitalName = "测试医院-level";
        String level = "四甲";
        String address = "嘉定区曹安公路2";
        BigDecimal latitude = new BigDecimal("30");
        BigDecimal longitude = new BigDecimal("120");
        String zipcode = "200062";
        String hospitalPhone = "120002";
        String outpatientHours = "8:00-17:00";
        String introduction = "测试非法的level";

        ResponseEntity<Response<String>> response = accountController.addHospital(hospitalName, level, address,
                latitude, longitude, zipcode,
                hospitalPhone, outpatientHours, introduction);

        Assertions.assertEquals("The level must equals to \"三甲\" or \"三乙\"",
                Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testAddHospitalRepeatedHospitalAddress() {
        String hospitalName = "测试医院-address";
        String level = "三甲";
        String address = "黄浦区重庆南路";
        BigDecimal latitude = new BigDecimal("30");
        BigDecimal longitude = new BigDecimal("120");
        String zipcode = "200062";
        String hospitalPhone = "120003";
        String outpatientHours = "8:00-17:00";
        String introduction = "测试重复的address";

        ResponseEntity<Response<String>> response = accountController.addHospital(hospitalName, level, address,
                latitude, longitude, zipcode,
                hospitalPhone, outpatientHours, introduction);

        Assertions.assertEquals("The hospital phone/name/address might have been used before!",
                Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testAddHospitalInvalidLatitude() {
        String hospitalName = "测试医院-latitude";
        String level = "三甲";
        String address = "嘉定区曹安公路4";
        BigDecimal latitude = new BigDecimal("99");
        BigDecimal longitude = new BigDecimal("120");
        String zipcode = "200062";
        String hospitalPhone = "120004";
        String outpatientHours = "8:00-17:00";
        String introduction = "测试越界的latitude";

        ResponseEntity<Response<String>> response = accountController.addHospital(hospitalName, level, address,
                latitude, longitude, zipcode,
                hospitalPhone, outpatientHours, introduction);

        Assertions.assertEquals("The latitude has exceeded the range [-90,90]",
                Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testAddHospitalInvalidLongitude() {
        String hospitalName = "测试医院-longitude";
        String level = "三甲";
        String address = "嘉定区曹安公路5";
        BigDecimal latitude = new BigDecimal("30");
        BigDecimal longitude = new BigDecimal("199");
        String zipcode = "200062";
        String hospitalPhone = "120005";
        String outpatientHours = "8:00-17:00";
        String introduction = "测试越界的longitude";

        ResponseEntity<Response<String>> response = accountController.addHospital(hospitalName, level, address,
                latitude, longitude, zipcode,
                hospitalPhone, outpatientHours, introduction);

        Assertions.assertEquals("The longitude has exceeded the range [-180,180]",
                Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testAddHospitalUnmatchedZipcode() {
        String hospitalName = "测试医院-zipcode";
        String level = "三甲";
        String address = "嘉定区曹安公路6";
        BigDecimal latitude = new BigDecimal("30");
        BigDecimal longitude = new BigDecimal("120");
        String zipcode = "123456789";
        String hospitalPhone = "120006";
        String outpatientHours = "8:00-17:00";
        String introduction = "测试不匹配的zipcode";

        ResponseEntity<Response<String>> response = accountController.addHospital(hospitalName, level, address,
                latitude, longitude, zipcode,
                hospitalPhone, outpatientHours, introduction);

        Assertions.assertEquals("The zipcode(CN) must contain 6 digits!",
                Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testAddHospitalRepeatedHospitalPhone() {
        String hospitalName = "测试医院-hospitalPhone";
        String level = "三甲";
        String address = "嘉定区曹安公路7";
        BigDecimal latitude = new BigDecimal("30");
        BigDecimal longitude = new BigDecimal("120");
        String zipcode = "200062";
        String hospitalPhone = "021120";
        String outpatientHours = "8:00-17:00";
        String introduction = "测试重复的hospitalPhone";

        ResponseEntity<Response<String>> response = accountController.addHospital(hospitalName, level, address,
                latitude, longitude, zipcode,
                hospitalPhone, outpatientHours, introduction);

        Assertions.assertEquals("The hospital phone/name/address might have been used before!",
                Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testAddHospitalUnmatchedOutpatientHour() {
        String hospitalName = "测试医院-outpatientHours";
        String level = "三甲";
        String address = "嘉定区曹安公路8";
        BigDecimal latitude = new BigDecimal("30");
        BigDecimal longitude = new BigDecimal("120");
        String zipcode = "200062";
        String hospitalPhone = "120008";
        String outpatientHours = "8:00--17:00";
        String introduction = "测试不匹配的outpatientHours";

        ResponseEntity<Response<String>> response = accountController.addHospital(hospitalName, level, address,
                latitude, longitude, zipcode,
                hospitalPhone, outpatientHours, introduction);

        Assertions.assertEquals("值班时间" + outpatientHours + "匹配失败！格式必须为xx:xx-xx:xx(可以为1位数字，但必须是标准的表示间隔的形式)",
                Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testDeleteHospitalInvalidHospitalID() {
        Integer hospitalId = 999;

        ResponseEntity<Response<String>> response = accountController.deleteHospital(hospitalId);

        Assertions.assertEquals("The Hospital " + hospitalId + " doesn't exist or has been removed earlier!",
                Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void getAccountList() {
        Response<List<DoctorInfoDTO>> response = accountController.getAccountList();

        Assertions.assertEquals(3, response.getResponse().size());
    }

    @Test
    void testAddAccountInvalidLength() throws JSONException, IOException {
        int hospitalId = 2;
        String idCard = "32072120020908421";
        String department = "Ear,Nose,Throat";
        String title = "director";
        String photoPath = "/data/0001.jpg";
        String contact = "02165990001";

        ResponseEntity<Response<String>> response = accountController.addAccount(hospitalId, idCard, department,
                title, photoPath, contact);

        Assertions.assertEquals("the length of ID must be 18 or 15",
                Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testAddAccountInvalidRegion() throws JSONException, IOException {
        int hospitalId = 2;
        String idCard = "111111200209084219";
        String department = "Ear,Nose,Throat";
        String title = "director";
        String photoPath = "/data/0002.jpg";
        String contact = "02165990002";

        ResponseEntity<Response<String>> response = accountController.addAccount(hospitalId, idCard, department,
                title, photoPath, contact);

        Assertions.assertEquals("There's no region code" + idCard.substring(0, 6),
                Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testAddAccountInvalidDate() throws JSONException, IOException {
        int hospitalId = 2;
        String idCard = "320721205001014219";
        String department = "Ear,Nose,Throat";
        String title = "director";
        String photoPath = "/data/0003.jpg";
        String contact = "02165990003";

        ResponseEntity<Response<String>> response = accountController.addAccount(hospitalId, idCard, department,
                title, photoPath, contact);

        Assertions.assertEquals("The date must reside before today!",
                Objects.requireNonNull(response.getBody()).getMessage());
    }

//    @Test
//    void testAddAccountInvalidDateFormat() throws JSONException, IOException {
//        int hospitalId = 2;
//        String idCard = "320721200209084210";
//        String department = "Ear,Nose,Throat";
//        String title = "director";
//        String photoPath = "/data/0004.jpg";
//        String contact = "02165990004";
//
//        ResponseEntity<Response<String>> response = accountController.addAccount(hospitalId, idCard, department,
//                title, photoPath, contact);
//
//        Assertions.assertEquals(idCard.substring(6, 14) + " doesn't adhere to the format of a valid birth date",
//                Objects.requireNonNull(response.getBody()).getMessage());
//    }

    @Test
    void testAddAccountInvalidCheckBit() throws JSONException, IOException {
        int hospitalId = 2;
        String idCard = "320721200209084210";
        String department = "Ear,Nose,Throat";
        String title = "director";
        String photoPath = "/data/0005.jpg";
        String contact = "02165990005";

        ResponseEntity<Response<String>> response = accountController.addAccount(hospitalId, idCard, department,
                title, photoPath, contact);

        Assertions.assertEquals("The ID is invalid though the region code and birth date is valid!",
                Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testAddAccountRepeatedContact() throws JSONException, IOException {
        int hospitalId = 2;
        String idCard = "320721200209084219";
        String department = "Ear,Nose,Throat";
        String title = "director";
        String photoPath = "/data/0006.jpg";
        String contact = "12345678913";
        try {
            ResponseEntity<Response<String>> response = accountController.addAccount(hospitalId, idCard, department,
                    title, photoPath, contact);

            Assertions.assertEquals("repeated contact is not allowed",
                    Objects.requireNonNull(response.getBody()).getMessage());
        }catch (Exception e){
                return;
        }
    }

    @Test
    void testAddAccountRepeatedIDCard() throws JSONException, IOException {
        int hospitalId = 2;
        String idCard = "320721200209084219";
        String department = "Ear,Nose,Throat";
        String title = "director";
        String photoPath = "/data/0007.jpg";
        String contact = "02165990007";
        try {
            ResponseEntity<Response<String>> response = accountController.addAccount(hospitalId, idCard, department,
                    title, photoPath, contact);

            Assertions.assertEquals("repeated ID card is not allowed",
                    Objects.requireNonNull(response.getBody()).getMessage());
        }catch (Exception e){
                return;
        }
    }

    @Test
    void testAddAccountInvalidHospitalID() throws JSONException, IOException {
        int hospitalId = 999;
        String idCard = "320721200009084214";
        String department = "Ear,Nose,Throat";
        String title = "director";
        String photoPath = "/data/0008.jpg";
        String contact = "02165990008";
        try {
            ResponseEntity<Response<String>> response = accountController.addAccount(hospitalId, idCard, department,
                    title, photoPath, contact);

            Assertions.assertEquals("hospital does not exist",
                    Objects.requireNonNull(response.getBody()).getMessage());
        }catch (Exception e){
                return;
        }
    }

    @Test
    void testDeleteAccountInvalidDoctorID() {
        int doctorId = 999;

        ResponseEntity<Response<String>> response = accountController.deleteAccount(doctorId);

        Assertions.assertEquals("The doctor account "+ doctorId +" has been removed",
                Objects.requireNonNull(response.getBody()).getMessage());
    }
}