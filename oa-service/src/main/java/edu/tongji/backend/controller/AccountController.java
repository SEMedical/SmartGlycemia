package edu.tongji.backend.controller;

import edu.tongji.backend.dto.DoctorInfoDTO;
import edu.tongji.backend.entity.Doctor;
import edu.tongji.backend.entity.Hospital;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.service.IAccountService;
import edu.tongji.backend.service.IHospitalService;
import edu.tongji.backend.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController  //用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/api/oa")  //对应的api路径
public class AccountController {
    @Autowired
    IAccountService accountService;
    @Autowired
    IHospitalService hospitalService;

    @PostMapping("/addHospital")
    public void addHospital(@RequestParam int hospital_id, @RequestParam String hospital_name, @RequestParam String level,
                            @RequestParam String address, @RequestParam BigDecimal latitude, @RequestParam BigDecimal longitude,
                            @RequestParam String zipcode, @RequestParam String hospital_phone, @RequestParam String outpatient_hour,
                            @RequestParam String introduction) {
        System.out.println("添加医院");
        Hospital hospital = new Hospital(hospital_id, hospital_name, level, address, latitude, longitude,
                                         zipcode, hospital_phone, outpatient_hour, introduction);
        hospitalService.addHospital(hospital);
        return;
    }

    @DeleteMapping("/removeHospital")
    public void deleteHospital(@RequestParam int hospital_id) {
//        医生对医院有外键依赖
        System.out.println("删除医院");
        hospitalService.deleteHospital(hospital_id);
        return;
    }

    @GetMapping("/getAccountList")
    public Response<List<DoctorInfoDTO>> getAccountList() {
        System.out.println("查看账号");
        return Response.success(accountService.getAccountList(),"return list success");
    }

    @PostMapping("/addAccount")
    public void addAccount(@RequestParam int doctor_id, @RequestParam int hospital_id, @RequestParam String id_card,
                           @RequestParam String department, @RequestParam String title, @RequestParam String photo_path, @RequestParam String contact) {
        System.out.println("添加账号");
        Doctor doctor = new Doctor(doctor_id, hospital_id, id_card, department, title, photo_path);
        accountService.addAccount(doctor, contact);
        return;
    }

    @DeleteMapping("/deleteAccount")
    public void deleteAccount(@RequestParam int doctor_id) {
        System.out.println("删除账号");
        accountService.deleteAccount(doctor_id);
        return;
    }
}
