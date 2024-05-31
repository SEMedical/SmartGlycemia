package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.entity.Hospital;
import edu.tongji.backend.mapper.HospitalMapper;
import edu.tongji.backend.service.IHospitalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
public class HospitalServiceImpl extends ServiceImpl<HospitalMapper, Hospital> implements IHospitalService {
    @Autowired
    HospitalMapper hospitalMapper;
    static Integer id;
    @Override
    public Integer addHospital(Hospital hospital) {
        try {
            id=hospitalMapper.getMaxId();
            synchronized (id) {
                hospital.setHospitalId(id + 1);
            }
            hospitalMapper.insert(hospital);
        }catch (Exception e){
            System.err.println(e.getMessage());
            throw e;
        }
        return id;
    }

    @Override
    public void deleteHospital(int hospitalId) {
        int i = hospitalMapper.deleteById(hospitalId);
        System.out.println(i);
        if(i==0)
            throw new NoSuchElementException("The Hospital "+hospitalId+" doesn't exist or has been removed earlier!");
    }
}
