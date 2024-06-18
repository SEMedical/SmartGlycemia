package edu.tongji.backend.service.impl;

import edu.tongji.backend.dto.PatientList;
import edu.tongji.backend.dto.SinglePatientInfo;
import edu.tongji.backend.dto.applyList;
import edu.tongji.backend.mapper.DoctorInteractMapper;
import edu.tongji.backend.service.DoctorInteractService;
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

import static edu.tongji.backend.util.RedisConstants.SUBSRIBE_DOCTOR_KEY;

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
    public Boolean confirmPatient(String messageId, String doctor_id) throws NullPointerException {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries("message:fromPatient:key:" + messageId);
        if(entries.size()==0){
            throw new NullPointerException("The message you found doesn't exist!");
        }
        String id = entries.get("id").toString();
        System.out.println(id);
        stringRedisTemplate.delete("message:from:patient:"+messageId);
        stringRedisTemplate.opsForList().remove("message:fromPatient:key:"+messageId,1,"message:from:patient:"+messageId);
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

}
