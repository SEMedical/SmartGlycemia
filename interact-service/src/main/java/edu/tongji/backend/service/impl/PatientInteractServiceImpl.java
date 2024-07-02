package edu.tongji.backend.service.impl;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 LEAVE-cshj,Dawson128
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */





import edu.tongji.backend.dto.DoctorDTO2;
import edu.tongji.backend.dto.PatientList;
import edu.tongji.backend.mapper.DoctorInteractMapper;
import edu.tongji.backend.mapper.PatientInteractMapper;
import edu.tongji.backend.mapper.SubscriptionMapper;
import edu.tongji.backend.service.PatientInteractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
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
    private static final DefaultRedisScript<String> SUB_SCRIPT;
    static {
        SUB_SCRIPT=new DefaultRedisScript<>();
        SUB_SCRIPT.setLocation(new ClassPathResource("subscribe.lua"));
        SUB_SCRIPT.setResultType(String.class);
    }
    public static final DefaultRedisScript<String> BOTH_DEC_SCRIPT;
    static {
        BOTH_DEC_SCRIPT=new DefaultRedisScript<>();
        BOTH_DEC_SCRIPT.setLocation(new ClassPathResource("both_inc_dec.lua"));
        BOTH_DEC_SCRIPT.setResultType(String.class);
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
        List<String> lists=new ArrayList<>();
        lists.add(SUBSRIBE_DOCTOR_KEY+doctorId);
        lists.add(FOLLOWER_KEY+format);
        PatientList patientInfo = doctorInteractMapper.getPatientInfo(String.valueOf(userId));
        stringRedisTemplate.execute(SUB_SCRIPT,lists,String.valueOf(userId),patientInfo.getPatientName()
        ,patientInfo.getPatientAge().toString(),String.valueOf(FOLLOWER_KEY_TTL*86400));
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
        List<String> lists=new ArrayList<>();
        lists.add(FOLLOWEES_NUM_KEY + user_id.toString());
        lists.add(FOLLOWERS_NUM_KEY + doctorId);
        stringRedisTemplate.execute(BOTH_DEC_SCRIPT,lists,"DECR");
        return subscriptionMapper.removeSubscription(String.valueOf(doctorId),user_id.toString());
    }

}
