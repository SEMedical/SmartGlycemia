package edu.tongji.backend.controller;

import edu.tongji.backend.dto.HospitalDTO;
import edu.tongji.backend.dto.Result;
import edu.tongji.backend.entity.Hospital;
import edu.tongji.backend.mapper.HospitalMapper;
import edu.tongji.backend.service.impl.HospitalServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hospital")
public class HospitalController {
    @Autowired
    HospitalMapper hospitalMapper;
    @Autowired
    HospitalServiceImpl hospitalService;
    @GetMapping
    public Result queryNearbyHospitals(
            @RequestParam(value="x",required = false) Double x,
            @RequestParam(value="y",required = false) Double y
    ){
        return hospitalService.queryHospital(1,x,y);
    }
}
