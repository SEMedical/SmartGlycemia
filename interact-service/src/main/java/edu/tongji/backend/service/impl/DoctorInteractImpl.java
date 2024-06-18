package edu.tongji.backend.service.impl;

import edu.tongji.backend.dto.PatientList;
import edu.tongji.backend.dto.SinglePatientInfo;
import edu.tongji.backend.dto.applyList;
import edu.tongji.backend.mapper.DoctorInteractMapper;
import edu.tongji.backend.service.DoctorInteractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import edu.tongji.backend.mapper.SubscriptionMapper;

import java.util.List;
import java.util.Map;
import edu.tongji.backend.mapper.DoctorInteractMapper;
import edu.tongji.backend.service.DoctorInteractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static edu.tongji.backend.util.RedisConstants.*;

@Slf4j
@Service
public class DoctorInteractImpl implements DoctorInteractService {
    @Autowired
    DoctorInteractMapper doctorInteractMapper;
    @Autowired
    SubscriptionMapper subscriptionMapper;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
//医生获取患者列表
    @Override
    public PatientList[] getPatientList() {
        PatientList[] p_list=doctorInteractMapper.getPatientList();
        return p_list;
    }

    @Override
    public SinglePatientInfo getSinglePatientInfo(String patientId) {
        SinglePatientInfo p_info=doctorInteractMapper.getSinglePatientInfo(patientId);
        return p_info;
    }

    @Override
    public Boolean confirmPatient(String messageId, String doctor_id) throws NullPointerException,IllegalArgumentException {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(FOLLOWER_KEY + messageId);
        if(entries.size()==0){
            throw new NullPointerException("The message you found doesn't exist!");
        }
        String id = entries.get("id").toString();
        log.info("The doctor"+doctor_id+" wants to confirm "+id);
        Integer subscribed = subscriptionMapper.Subscribed(doctor_id, id);
        stringRedisTemplate.delete(FOLLOWER_KEY+messageId);
        stringRedisTemplate.opsForList().remove(SUBSRIBE_DOCTOR_KEY+doctor_id,0,FOLLOWER_KEY+messageId);
        if(subscribed>0){
            throw new IllegalArgumentException("The patient "+id+" has followed you!");
        }
        stringRedisTemplate.opsForValue().increment(FOLLOWEES_NUM_KEY + id);
        stringRedisTemplate.opsForValue().increment(FOLLOWERS_NUM_KEY + doctor_id);
        return subscriptionMapper.addSubscription(doctor_id,id);
    }

    @Override
    public applyList[] doctorGetApplicationList(String doctorId) {
        List<String> messages = stringRedisTemplate.opsForList().range(SUBSRIBE_DOCTOR_KEY + doctorId, 0, -1);

        applyList[] applyList = new applyList[messages.size()];
        System.out.println(messages);

        for (int i = 0; i < messages.size(); i++) {
            String message = messages.get(i);
            // 获取 message 对应的数据
            Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(message);
            try {
                String id = entries.get("id").toString();
                String name = entries.get("name").toString();
                String age = entries.get("age").toString();
                message = message.substring(SUBSRIBE_DOCTOR_KEY.length() - 2);
                applyList[i] = new applyList(message, id, name, age);
            }catch (Exception e){
                stringRedisTemplate.opsForList().remove(SUBSRIBE_DOCTOR_KEY+doctorId,0,message);
                applyList[i]=null;
            }

        }
        return applyList;
    }

    @Override
    public Integer getFollowersNum(String doctor_id) {
        String s = stringRedisTemplate.opsForValue().get(FOLLOWERS_NUM_KEY + doctor_id);
        if(s!=null&&s.length()!=0&&(!s.equals(""))){
            return Integer.valueOf(s);
        }
        return subscriptionMapper.FollowerNum(doctor_id);
    }
    @Override
    public Integer getFolloweesNum(String user_id) {
        String s = stringRedisTemplate.opsForValue().get(FOLLOWEES_NUM_KEY + user_id);
        if(s!=null&&s.length()!=0&&(!s.equals(""))){
            return Integer.valueOf(s);
        }
        return subscriptionMapper.FolloweeNum(user_id);
    }
}
