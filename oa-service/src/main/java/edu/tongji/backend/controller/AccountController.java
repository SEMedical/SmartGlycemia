package edu.tongji.backend.controller;

import edu.tongji.backend.entity.Doctor;
import edu.tongji.backend.service.IAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController  //用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/api/oa")  //对应的api路径
public class AccountController {
    @Autowired
    IAccountService accountService;

    @GetMapping("/getAccountList")
    public List<Doctor> getAccountList() {
        System.out.println("查看账号");
        return accountService.getAccountList();
    }

    @PostMapping("/addAccount")
    public void addAccount(@RequestParam int doctorId, @RequestParam int hospitalId, @RequestParam String idCard,
                           @RequestParam String department, @RequestParam String title, @RequestParam String photoPath) {
        System.out.println("添加账号");
        Doctor doctor = new Doctor(doctorId, hospitalId, idCard, department, title, photoPath);
        accountService.addAccount(doctor);
        return;
    }

    @PostMapping("/deleteAccount")
    public void deleteAccount(@RequestParam int doctorId) {
        System.out.println("删除账号");
        accountService.deleteAccount(doctorId);
        return;
    }
}
