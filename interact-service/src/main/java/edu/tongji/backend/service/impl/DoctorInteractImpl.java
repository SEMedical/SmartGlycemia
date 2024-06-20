package edu.tongji.backend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.emory.mathcs.backport.java.util.Arrays;
import edu.tongji.backend.dto.PatientList;
import edu.tongji.backend.dto.SinglePatientInfo;
import edu.tongji.backend.dto.applyList;
import edu.tongji.backend.mapper.DoctorInteractMapper;
import edu.tongji.backend.service.DoctorInteractService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.list.TreeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import edu.tongji.backend.mapper.SubscriptionMapper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.tongji.backend.mapper.DoctorInteractMapper;
import edu.tongji.backend.service.DoctorInteractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static edu.tongji.backend.service.impl.PatientInteractServiceImpl.BOTH_DEC_SCRIPT;
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
        List<String> lists=new ArrayList<>();
        lists.add(FOLLOWER_KEY);
        lists.add(SUBSRIBE_DOCTOR_KEY);
        stringRedisTemplate.execute(REM_APPL_SCRIPT, lists, messageId,doctor_id);
        if(subscribed>0){
            throw new IllegalArgumentException("The patient "+id+" has followed you!");
        }
        //Make the increment of followers and followees atomic
        List<String> lists2=new ArrayList<>();
        lists2.add(FOLLOWEES_NUM_KEY + id);
        lists2.add(FOLLOWERS_NUM_KEY + doctor_id);
        stringRedisTemplate.execute(BOTH_DEC_SCRIPT,lists2,"INCR");
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

    @Override
    public List<PatientList> getFollowerList(String doctor_id) throws JsonProcessingException {
        Long l = stringRedisTemplate.opsForZSet().zCard(FOLLOWER_LIST_KEY + doctor_id);
        String s = stringRedisTemplate.opsForValue().get(FOLLOWEES_NUM_KEY + doctor_id);
        List<PatientList> res=new ArrayList<>();
        if(s!=null&&Long.valueOf(s).equals(l)) {
            Set<String> followers = stringRedisTemplate.opsForZSet().range(FOLLOWER_LIST_KEY + doctor_id, 0, -1);
            for (String follower : followers) {
                PatientList patientList = new PatientList(follower);
                res.add(patientList);
            }
            return res;
        }else{
            List<PatientList> followerList = doctorInteractMapper.getFollowerList(doctor_id);
            stringRedisTemplate.delete(FOLLOWER_LIST_KEY+doctor_id);
            for (PatientList patientList : followerList) {
                stringRedisTemplate.opsForZSet().add(FOLLOWER_LIST_KEY+doctor_id,patientList.toString(), Timestamp.valueOf(patientList.getTimestamp()).getTime());
            }
            return followerList;
        }
    }
    private static final DefaultRedisScript<String> REM_APPL_SCRIPT;
    static {
        REM_APPL_SCRIPT=new DefaultRedisScript<>();
        REM_APPL_SCRIPT.setLocation(new ClassPathResource("rem_appl.lua"));
        REM_APPL_SCRIPT.setResultType(String.class);
    }
    @Override
    public Boolean discardPatient(String messageId, String doctor_id) {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(FOLLOWER_KEY + messageId);
        if(entries.size()==0){
            throw new NullPointerException("The message you found doesn't exist!");
        }
        String id = entries.get("id").toString();
        log.info("The doctor"+doctor_id+" wants to discard "+id);
        List<String> lists=new ArrayList<>();
        lists.add(FOLLOWER_KEY);
        lists.add(SUBSRIBE_DOCTOR_KEY);
        stringRedisTemplate.execute(REM_APPL_SCRIPT, lists, messageId,doctor_id);
        return true;
    }
}
