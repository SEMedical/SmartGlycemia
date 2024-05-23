package edu.tongji.backend.controller;

import edu.tongji.backend.entity.Doctor;
import edu.tongji.backend.service.PatientInteractService;
import edu.tongji.backend.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController//用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/api/interaction")//对应的api路径
public class PatientInteractController {
    @Autowired
    PatientInteractService patientInteractService;

    /*
        @Description:查找医生
        Since released:2.2.0
     */
    @GetMapping("/getDoctor")
    public ResponseEntity<Response<List<Doctor>>> SearchDoctor(@RequestParam("keyword") String keyword){

        //通过查找医生姓名、医院名称、科室、医生联系方式、医院地址等方式
        List<Doctor> D =patientInteractService.searchAll(keyword);
        //查找失败
        if(D.isEmpty())
            return new ResponseEntity<>(Response.fail("查无此人！"), HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(Response.success(D,"查找成功！"),HttpStatus.OK);

    }

    @PostMapping("/subscribeDoctor")
    //患者向医生提交好友申请
    public ResponseEntity<Response<Void>> SubscribeDoctor(@RequestParam("doctor_id") int doctor_id){
        try {
            patientInteractService.subscribeDoctor(1, doctor_id);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(Response.fail("Redis connect failed"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(Response.success(null,"subscribed successfully"),HttpStatus.NO_CONTENT);
    }

    @PostMapping("/appointDoctorByDept")
    //预约医生（按照科室）
    public Response<Void> AppointDoctor(@RequestParam("department") String department,@RequestParam("datetime") String datetime,@RequestParam("hospital_id") int hospital_id){
        patientInteractService.appointDoctor(department,datetime,hospital_id);
        return null;
    }
}
