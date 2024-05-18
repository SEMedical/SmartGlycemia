package edu.tongji.backend.controller;

import edu.tongji.backend.dto.DoctorInfoDTO;
import edu.tongji.backend.entity.Doctor;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.service.IAccountService;
import edu.tongji.backend.util.Response;
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
//    join user表 除了密码和角色
    public Response<List<DoctorInfoDTO>> getAccountList() {
        System.out.println("查看账号");
        return Response.success(accountService.getAccountList(),"return list success");
    }

    @PostMapping("/addAccount")
//    自动生成ID
//    同时创建user，role为doctor，默认密码idCard后六位，加密sha256，验证数据有效性，（加拦截器后）请求头保存管理员useId，身份是admin，
//    错误处理：log4j 接口 sl4j 实现，低耦合

    public void addAccount(@RequestParam int doctorId, @RequestParam int hospitalId, @RequestParam String idCard,
                           @RequestParam String department, @RequestParam String title, @RequestParam String photoPath) {
        System.out.println("添加账号");
        Doctor doctor = new Doctor(doctorId, hospitalId, idCard, department, title, photoPath);
        accountService.addAccount(doctor);
        return;
    }

    @PostMapping("/deleteAccount")
//    删 doctor 和 user，事务？
    public void deleteAccount(@RequestParam int doctorId) {
        System.out.println("删除账号");
        accountService.deleteAccount(doctorId);
        return;
    }
}
