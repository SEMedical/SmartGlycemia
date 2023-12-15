package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.entity.HeartRate;
import edu.tongji.backend.mapper.HeartRateMapper;
import edu.tongji.backend.service.IHeartRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HeartRateServiceImpl extends ServiceImpl<HeartRateMapper, HeartRate> implements IHeartRateService {
    @Autowired
    HeartRateMapper heartRateMapper;
}
