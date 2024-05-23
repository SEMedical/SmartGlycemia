package edu.tongji.backend.controller;

import edu.tongji.backend.dto.PatientList;
import edu.tongji.backend.dto.SinglePatientInfo;
import edu.tongji.backend.util.Response;
import edu.tongji.backend.service.DoctorInteractService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/getPatientInfo")
    public Response<SinglePatientInfo> doctorGetSinglePatientInfo(String patientId) {
        SinglePatientInfo p_info=doctorInteractService.getSinglePatientInfo(patientId);
        return Response.success(p_info, "success");
    }


}
