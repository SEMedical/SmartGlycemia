package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.dto.DoctorDTO;
import edu.tongji.backend.entity.Glycemia;
import edu.tongji.backend.mapper.PatientInteractMapper;
import edu.tongji.backend.service.PatientInteractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class PatientInteractServiceImpl implements PatientInteractService {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PatientInteractMapper patientInteractMapper;

    @Override
    public List<DoctorDTO> searchAll(String keyword) {
        List<DoctorDTO> D=patientInteractMapper.searchAll(keyword);
        return D;
    }

    @Override
    public void subscribeDoctor(int userId,int doctorId) {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        String format = formatter.format(date);
        stringRedisTemplate.opsForList().leftPush("subscription:toDoctor:key:"+doctorId,"message:fromPatient:key:"+format);
        Map<String,String> maps=new TreeMap<>();
        maps.put("id", String.valueOf(userId));//Scalability can't change it to KV
        stringRedisTemplate.opsForHash().putAll("message:fromPatient:key:"+format,maps);
    }

    @Override
    public void appointDoctor(String department, String datetime, int hospitalId) {
       // patientInteractMapper.appointDoctor(department,datetime,hospitalId);
    }

}
