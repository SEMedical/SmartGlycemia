package edu.tongji.backend.service.impl;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 Victor Hu
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




import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.dto.*;
import edu.tongji.backend.entity.Profile;
import edu.tongji.backend.entity.Questionnaire;
import edu.tongji.backend.mapper.QuestionnaireMapper;
import edu.tongji.backend.service.IQuestionnaireService;
import edu.tongji.backend.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.ArrayList;
@Slf4j
@Service
public class QuestionnaireServiceImpl extends ServiceImpl<QuestionnaireMapper, Questionnaire> implements IQuestionnaireService {
    @Autowired
    QuestionnaireMapper questionnaireMapper;

    @Override
    public List<String> getQuestionnaire1(){
        /* 您有增殖性视网膜病变或者肾病吗？
        您有外周血管病变
        自主性神经病变
        糖尿病视网膜病变
        保护性感觉丧失（运动不超过20分钟）
        糖尿病周围神经病变（只保留步行，因为不能过度伸展）*/
        List<String> question = new ArrayList<>();
        question.add("您有增殖性视网膜病变或者肾病吗？");
        question.add("您有外周血管病变吗？");
        question.add("您有自主性神经病变吗？");
        question.add("您有糖尿病视网膜病变吗？");
        question.add("您有保护性感觉丧失吗？");
        question.add("您有糖尿病周围神经病变吗？");
        return question;
    }
    @Resource
    RestTemplate restTemplate;
    @Override
    public List<QuestionnaireDTO> getQuestionnaire2(Integer userId, List<Boolean> answer){
        if (!insertQuestionnaire1(userId, answer)) {
            return null;
        }
        String url = "http://localhost:81/api/health/getProfile?patient_id="+userId;
        // 2.2.发送http请求，实现远程调用
        Response<Profile> profile0 = restTemplate.getForObject(url, Response.class);
        Profile profile=profile0.getResponse();
        String gender = profile.getGender();

        /* 慢跑禁忌症：
        严重的视力问题，如青光眼
        女性怀孕期间
        呼吸系统疾病
        循环系统疾病
        严重超重
        膝关节损伤

        瑜伽禁忌症：
        骨质疏松
        颈椎病
        腰椎病
        女性怀孕期间

        跳绳禁忌症：
        老年人、骨质疏松者
        静脉曲张患者
        过重人群
        膝盖旧伤未愈
        女性怀孕期间 */

        List<QuestionnaireDTO> question = new ArrayList<>();

        question.add(new QuestionnaireDTO(1, "您经常运动吗？"));

        // 如果有糖尿病周围神经病变，只保留步行，不需要询问其他信息
        if (answer.get(5)) {
            return question;
        }

        question.add(new QuestionnaireDTO(2, "您有骨质疏松吗？"));
        question.add(new QuestionnaireDTO(3, "您有颈椎病吗？"));
        question.add(new QuestionnaireDTO(4, "您有腰椎病吗？"));
        if (gender.equals("Female")) {
            question.add(new QuestionnaireDTO(5, "您处在怀孕期间吗？"));
        }

        if (!answer.get(0) && !answer.get(1) && !answer.get(2) && !answer.get(3) && !answer.get(4)) {
            question.add(new QuestionnaireDTO(6, "您有严重的视力问题（如青光眼）吗？"));
            question.add(new QuestionnaireDTO(7, "您有呼吸系统疾病吗？"));
            question.add(new QuestionnaireDTO(8, "您有循环系统疾病吗？"));
            question.add(new QuestionnaireDTO(9, "您超重吗？"));
            question.add(new QuestionnaireDTO(10, "您有膝关节损伤吗？"));
            question.add(new QuestionnaireDTO(11, "您有静脉曲张吗？"));
        }
        return question;
    }

    private boolean insertQuestionnaire1(Integer userId, List<Boolean> answer){
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setPatientId(userId);
        questionnaire.setVersion("1.0.0");
        questionnaire.setTemplate(1);
        StringBuilder answerString = new StringBuilder();
        for (Boolean aBoolean : answer) {
            if (aBoolean) {
                answerString.append("1");
            } else {
                answerString.append("0");
            }
        }
        questionnaire.setData(answerString.toString());

        questionnaireMapper.insert(questionnaire);
        return true;
    }

    @Override
    public ScenarioDTO getRecommendedSportPlan(Integer userId, List<Answer2> answer,String token){
        if (!insertQuestionnaire2(userId, answer)) {
            return null;
        }

//        boolean walking = true;
        boolean yoga = true;
        boolean jogging = true;
        boolean ropeSkipping = true;

        int walkingDuration = 30;
        int yogaDuration = 30;
        int joggingDuration = 20;
        int ropeSkippingDuration = 10;

//        log.info(yoga);
//        log.info(jogging);
//        log.info(ropeSkipping);
//        log.info("**********");

        String questionnaire1Answer = questionnaireMapper.selectByPatientIdAndTemplate(userId, 1);
        if (questionnaire1Answer.charAt(4) == '1') {
            walkingDuration = 15;
            yogaDuration = 15;
            jogging = false;
            ropeSkipping = false;
        }
        if (questionnaire1Answer.charAt(5) == '1') {
            yoga = false;
            jogging = false;
            ropeSkipping = false;
        }

//        log.info(yoga);
//        log.info(jogging);
//        log.info(ropeSkipping);
//        log.info("**********");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", token);
        HttpEntity entity = new HttpEntity<>(headers);
        String url = "http://localhost:81/api/health/getProfile?patient_id="+userId;
        // 2.2.发送http请求，实现远程调用
        Response<Profile> profile0 = restTemplate.getForObject(url, Response.class);
        Profile profile=profile0.getResponse();
        Integer age = profile.getAge();
        if (age >= 60) {
            ropeSkipping = false;
        }

//        log.info(yoga);
//        log.info(jogging);
//        log.info(ropeSkipping);
//        log.info("**********");

        for (Answer2 answer2 : answer) {
            if (answer2.getQuestion_no() == 1) {
                if (!answer2.getQuestion_ans()) {
                    walkingDuration = 10;
                    yogaDuration = 10;
                    joggingDuration = 10;
                    ropeSkippingDuration = 5;
                }
            } else if (answer2.getQuestion_no() == 2) {
                if (answer2.getQuestion_ans()) {
                    yoga = false;
                    ropeSkipping = false;
                }
            } else if (answer2.getQuestion_no() == 3) {
                if (answer2.getQuestion_ans()) {
                    yoga = false;
                }
            } else if (answer2.getQuestion_no() == 4) {
                if (answer2.getQuestion_ans()) {
                    yoga = false;
                }
            } else if (answer2.getQuestion_no() == 5) {
                if (answer2.getQuestion_ans()) {
                    yoga = false;
                    jogging = false;
                    ropeSkipping = false;
                }
            } else if (answer2.getQuestion_no() == 6) {
                if (answer2.getQuestion_ans()) {
                    jogging = false;
                }
            } else if (answer2.getQuestion_no() == 7) {
                if (answer2.getQuestion_ans()) {
                    jogging = false;
                }
            } else if (answer2.getQuestion_no() == 8) {
                if (answer2.getQuestion_ans()) {
                    jogging = false;
                }
            } else if (answer2.getQuestion_no() == 9) {
                if (answer2.getQuestion_ans()) {
                    jogging = false;
                    ropeSkipping = false;
                }
            } else if (answer2.getQuestion_no() == 10) {
                if (answer2.getQuestion_ans()) {
                    jogging = false;
                    ropeSkipping = false;
                }
            } else if (answer2.getQuestion_no() == 11) {
                if (answer2.getQuestion_ans()) {
                    ropeSkipping = false;
                }
            }
        }

        List<Sport> sports = new ArrayList<>();
        sports.add(new Sport("散步", walkingDuration));
        if (yoga) {
            sports.add(new Sport("瑜伽", yogaDuration));
        }
        if (jogging) {
            sports.add(new Sport("慢跑", joggingDuration));
        }
        if (ropeSkipping) {
            sports.add(new Sport("跳绳", ropeSkippingDuration));
        }
        return new ScenarioDTO(sports);
    }

    private boolean insertQuestionnaire2(Integer userId, List<Answer2> answer){
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setPatientId(userId);
        questionnaire.setVersion("1.0.0");
        questionnaire.setTemplate(2);
        StringBuilder answerString = new StringBuilder();
        List<Integer> questionNo = new ArrayList<>();
        for (Answer2 answer2 : answer) {
            questionNo.add(answer2.getQuestion_no());
        }

        log.info(questionNo.toString());

        int count = 0;
        for (int i = 1; i <= 11; i++) {
            if (questionNo.contains(i)) {
                if (answer.get(count).getQuestion_ans()) {
                    answerString.append("1");
                } else {
                    answerString.append("0");
                }
                count++;
            } else {
                answerString.append("x");
            }
        }

        questionnaire.setData(answerString.toString());

        questionnaireMapper.insert(questionnaire);
        return true;
    }
}
