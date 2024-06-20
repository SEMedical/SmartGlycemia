package edu.tongji.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.tongji.backend.dto.PatientList;
import edu.tongji.backend.dto.UserDTO;
import edu.tongji.backend.dto.applyList;
import edu.tongji.backend.dto.SinglePatientInfo;
import edu.tongji.backend.util.Response;
import edu.tongji.backend.service.DoctorInteractService;
import edu.tongji.backend.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import org.springframework.web.bind.annotation.*;



@Slf4j
@RestController
@RequestMapping("/api/interaction")
public class DoctorInteractController {
    @Autowired
    private DoctorInteractService doctorInteractService;
    //医生获取患者列表
    @GetMapping("/getPatientList")
    public Response<PatientList[]> doctorGetPatientList() {
        PatientList[] p_list = doctorInteractService.getPatientList();
        return Response.success(p_list, "success");
    }
    @GetMapping("/getFollowerList")
    public Response<List<PatientList>> doctorGetFollowerList() throws JsonProcessingException {
        UserDTO user= UserHolder.getUser();
        String user_id= user.getUserId();
        List<PatientList> p_list = doctorInteractService.getFollowerList(user_id);
        return Response.success(p_list, "success");
    }
    @GetMapping("/followerNum")
    public Response<Integer> followerTotal(){
        UserDTO user= UserHolder.getUser();
        String user_id= user.getUserId();
        Integer followersNum = doctorInteractService.getFollowersNum(user_id);
        return Response.success(followersNum,"The total followers of "+user_id+" has returned!");
    }
    @GetMapping("/patient/followeeNum")
    public Response<Integer> followeeTotal(){
        UserDTO user= UserHolder.getUser();
        String user_id= user.getUserId();
        Integer followeesNum = doctorInteractService.getFolloweesNum(user_id);
        return Response.success(followeesNum,"The total followees of "+user_id+" has returned!");
    }
//  医生确认患者申请
    @PostMapping("/confirmPatient")
    public ResponseEntity<Response<String>> doctorConfirmPatient(@RequestParam("messageId") String messageId){
        UserDTO user= UserHolder.getUser();
        String user_id= user.getUserId();
        String result="confirm";
        try {
            doctorInteractService.confirmPatient(messageId, user_id);
        }catch (DuplicateKeyException e){
            return new ResponseEntity<>(Response.fail(e.getMessage()),HttpStatus.BAD_REQUEST);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(Response.fail(e.getMessage()),HttpStatus.OK);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(Response.fail(e.getMessage()),HttpStatus.OK);
        }
        return new ResponseEntity<>(Response.success(result, "success"),HttpStatus.OK);
    }
    @CrossOrigin
    @DeleteMapping("/discardPatient")
    public ResponseEntity<Response<String>> doctorDiscardPatient(@RequestParam("messageId") String messageId){
        UserDTO user= UserHolder.getUser();
        String user_id= user.getUserId();
        String result="discard";
        try {
            doctorInteractService.discardPatient(messageId, user_id);
        }catch (NullPointerException e) {
            return new ResponseEntity<>(Response.fail(e.getMessage()),HttpStatus.OK);
        }
        return new ResponseEntity<>(Response.success(result, "success"),HttpStatus.OK);
    }

//    医生获取申请列表
    @GetMapping("/getApplicationList")
    public  Response<applyList[]> doctorGetApplicationList(){
        UserDTO user= UserHolder.getUser();
        String user_id= user.getUserId();
        applyList[] p_list = doctorInteractService.doctorGetApplicationList(user_id);
        return Response.success(p_list, "success");
    }
    @GetMapping("/getPatientInfo")
    public Response<SinglePatientInfo> doctorGetSinglePatientInfo(String patientId) {
        SinglePatientInfo p_info=doctorInteractService.getSinglePatientInfo(patientId);
        return Response.success(p_info, "success");
    }


}
