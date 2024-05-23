package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.entity.Hospital;
import edu.tongji.backend.mapper.HospitalMapper;
import edu.tongji.backend.service.IHospitalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HospitalServiceImpl extends ServiceImpl<HospitalMapper, Hospital> implements IHospitalService {
    @Autowired
    HospitalMapper hospitalMapper;

    @Override
    public void addHospital(Hospital hospital) {
        hospitalMapper.insert(hospital);
        return;
    }

    @Override
    public void deleteHospital(int hospitalId) {
        hospitalMapper.deleteById(hospitalId);
        return;
    }
}
