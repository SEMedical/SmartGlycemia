package edu.tongji.backend.service.impl;

import edu.tongji.backend.dto.DoctorDTO2;
import edu.tongji.backend.dto.PatientList;
import edu.tongji.backend.mapper.DoctorInteractMapper;
import edu.tongji.backend.mapper.PatientInteractMapper;
import edu.tongji.backend.mapper.SubscriptionMapper;
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
import java.util.concurrent.TimeUnit;

import static edu.tongji.backend.util.RedisConstants.*;

@Service
public class PatientInteractServiceImpl implements PatientInteractService {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PatientInteractMapper patientInteractMapper;
    @Autowired
    private DoctorInteractMapper doctorInteractMapper;
    @Autowired
    private SubscriptionMapper subscriptionMapper;
    @Override
    public List<DoctorDTO2> searchAll(String keyword) {
        List<DoctorDTO2> D=patientInteractMapper.searchAll(keyword);
        return D;
    }

    @Override
    public void subscribeDoctor(int userId,int doctorId) throws IllegalArgumentException {
        Integer subscribed = subscriptionMapper.Subscribed(String.valueOf(doctorId), String.valueOf(userId));
        if(subscribed>0){
            throw new IllegalArgumentException("You've followed the Dr. "+doctorId);
        }
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        String format = formatter.format(date);
        stringRedisTemplate.opsForList().leftPush(SUBSRIBE_DOCTOR_KEY+doctorId,FOLLOWER_KEY+format);
        Map<String,String> maps=new TreeMap<>();
        maps.put("id", String.valueOf(userId));//Scalability can't change it to KV
        PatientList patientInfo = doctorInteractMapper.getPatientInfo(String.valueOf(userId));
        if(patientInfo!=null) {
            maps.put("name", patientInfo.getPatientName());
            if (patientInfo.getPatientAge() != null)
                maps.put("age", patientInfo.getPatientAge().toString());
        }
        stringRedisTemplate.expire(FOLLOWER_KEY+format,FOLLOWER_KEY_TTL, TimeUnit.DAYS);
        stringRedisTemplate.opsForHash().putAll(FOLLOWER_KEY+format,maps);
    }

    @Override
    public void appointDoctor(String department, String datetime, int hospitalId) {
       // patientInteractMapper.appointDoctor(department,datetime,hospitalId);
    }

    @Override
    public Boolean unsubscribeDoctor(Integer user_id, int doctorId) {
        Integer subscribed = subscriptionMapper.Subscribed(String.valueOf(doctorId), String.valueOf(user_id));
        if(subscribed==0){
            throw new IllegalArgumentException("You don't follow the "+doctorId);
        }
        stringRedisTemplate.opsForValue().decrement(FOLLOWEES_NUM_KEY + user_id.toString());
        stringRedisTemplate.opsForValue().decrement(FOLLOWERS_NUM_KEY + doctorId);
        return subscriptionMapper.removeSubscription(user_id.toString(),String.valueOf(doctorId));
    }

}
