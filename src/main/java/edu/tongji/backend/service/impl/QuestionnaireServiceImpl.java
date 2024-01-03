package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.dto.*;
import edu.tongji.backend.entity.Questionnaire;
import edu.tongji.backend.mapper.QuestionnaireMapper;
import edu.tongji.backend.service.IProfileService;
import edu.tongji.backend.service.IQuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
public class QuestionnaireServiceImpl extends ServiceImpl<QuestionnaireMapper, Questionnaire> implements IQuestionnaireService {
    @Autowired
    QuestionnaireMapper questionnaireMapper;
    @Autowired
    IProfileService profileService;

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

    @Override
    public List<QuestionnaireDTO> getQuestionnaire2(Integer userId, List<Boolean> answer){
        if (!insertQuestionnaire1(userId, answer)) {
            return null;
        }
        String gender = profileService.getByPatientId(userId.toString()).getGender();

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
    public ScenarioDTO getRecommendedSportPlan(Integer userId, List<Answer2> answer){
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

//        System.out.println(yoga);
//        System.out.println(jogging);
//        System.out.println(ropeSkipping);
//        System.out.println("**********");

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

//        System.out.println(yoga);
//        System.out.println(jogging);
//        System.out.println(ropeSkipping);
//        System.out.println("**********");

        Integer age = profileService.getByPatientId(userId.toString()).getAge();
        if (age >= 60) {
            ropeSkipping = false;
        }

//        System.out.println(yoga);
//        System.out.println(jogging);
//        System.out.println(ropeSkipping);
//        System.out.println("**********");

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

        System.out.println(questionNo);

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
