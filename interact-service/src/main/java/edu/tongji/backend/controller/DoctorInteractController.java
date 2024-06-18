package edu.tongji.backend.controller;

import edu.tongji.backend.dto.PatientList;
import edu.tongji.backend.dto.UserDTO;
import edu.tongji.backend.dto.applyList;
import edu.tongji.backend.dto.SinglePatientInfo;
import edu.tongji.backend.util.Response;
import edu.tongji.backend.service.DoctorInteractService;
import edu.tongji.backend.util.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/api/interaction")
@CrossOrigin
public class DoctorInteractController {
    @Autowired
    private DoctorInteractService doctorInteractService;
    //医生获取患者列表
    @GetMapping("/getPatientList")
    public Response<PatientList[]> doctorGetPatientList() {
        PatientList[] p_list = doctorInteractService.getPatientList();
        return Response.success(p_list, "success");
    }
//  医生确认患者申请
    @PostMapping("/confirmPatient")
    public ResponseEntity<Response<String>> doctorConfirmPatient(@RequestParam("messageId") String messageId){
        String result="confirm";
        //UserDTO user =new UserDTO(null,"SHJ","121","doctor");
        try {
            doctorInteractService.confirmPatient(messageId, "121");
        }catch (DuplicateKeyException e){
            return new ResponseEntity<>(Response.fail(e.getMessage().substring(0,100)),HttpStatus.BAD_REQUEST);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(Response.fail(e.getMessage().substring(0,100)),HttpStatus.NOT_FOUND);
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
