package edu.tongji.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.dto.Result;
import edu.tongji.backend.entity.HeartRate;
import edu.tongji.backend.entity.Hospital;

import java.util.List;

public interface IHospitalService extends IService<Hospital> {
    Result queryHospital(Integer current, Double x, Double y);
}
