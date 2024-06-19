package edu.tongji.backend.controller;

import edu.tongji.backend.dto.DoctorDTO2;
import edu.tongji.backend.dto.UserDTO;
import edu.tongji.backend.service.PatientInteractService;
import edu.tongji.backend.util.Response;
import edu.tongji.backend.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController//用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/api/interaction")//对应的api路径
public class PatientInteractController {
    @Autowired
    PatientInteractService patientInteractService;

    /**
     *
     * @param keyword
     * @return
     * @since 2.1.0
     */
    @GetMapping("/getDoctor")
    public ResponseEntity<Response<List<DoctorDTO2>>> SearchDoctor(@RequestParam("keyword") String keyword){

        //通过查找医生姓名、医院名称、科室、医生联系方式、医院地址等方式
        List<DoctorDTO2> D =patientInteractService.searchAll(keyword);
        //查找失败
        if(D.isEmpty())
            return new ResponseEntity<>(Response.fail("查无此人！"), HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(Response.success(D,"查找成功！"),HttpStatus.OK);

    }
    @CrossOrigin(origins = "*")
    @DeleteMapping("/patient/unsubscribeDoctor")
    //患者向医生提交好友申请
    public ResponseEntity<Response<Void>> UnsubscribeDoctor(@RequestParam("doctor_id") int doctor_id){
        UserDTO user= UserHolder.getUser();
        String user_id= user.getUserId();
        try {
            patientInteractService.unsubscribeDoctor( Integer.valueOf(user_id),doctor_id);
        }catch (Exception e){
            e.printStackTrace();
            if(e instanceof IllegalArgumentException){
                return new ResponseEntity<>(Response.fail(e.getMessage()),HttpStatus.OK);
            }else
                return new ResponseEntity<>(Response.fail("Redis connect failed"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(Response.success(null,"unsubscribed successfully"),HttpStatus.NO_CONTENT);
    }
    @CrossOrigin(origins = "*")
    @DeleteMapping("/firePatient")
    //患者向医生提交好友申请
    public ResponseEntity<Response<Void>> FirePatient(@RequestParam("patient_id") int patient_id){
        UserDTO user= UserHolder.getUser();
        String user_id= user.getUserId();
        try {
            patientInteractService.unsubscribeDoctor(patient_id,Integer.valueOf(user_id));
        }catch (Exception e){
            e.printStackTrace();
            if(e instanceof IllegalArgumentException){
                return new ResponseEntity<>(Response.fail(e.getMessage()),HttpStatus.OK);
            }else
                return new ResponseEntity<>(Response.fail("Redis connect failed"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(Response.success(null,"Kicked successfully"),HttpStatus.NO_CONTENT);
    }
    @PostMapping("/patient/subscribeDoctor")
    //患者向医生提交好友申请
    public ResponseEntity<Response<Void>> SubscribeDoctor(@RequestParam("doctor_id") int doctor_id){
        UserDTO user= UserHolder.getUser();
        String user_id= user.getUserId();
        try {
            patientInteractService.subscribeDoctor(Integer.valueOf(user_id), doctor_id);
        }catch (Exception e){
            e.printStackTrace();
            if(e instanceof IllegalArgumentException){
                return new ResponseEntity<>(Response.fail(e.getMessage()),HttpStatus.OK);
            }else
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
