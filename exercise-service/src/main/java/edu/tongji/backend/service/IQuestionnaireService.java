package edu.tongji.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.dto.Answer2;
import edu.tongji.backend.dto.QuestionnaireDTO;
import edu.tongji.backend.dto.ScenarioDTO;
import edu.tongji.backend.dto.SportPlanDTO;
import edu.tongji.backend.entity.Questionnaire;

import java.util.List;

public interface IQuestionnaireService extends IService<Questionnaire> {
    List<String> getQuestionnaire1();
    List<QuestionnaireDTO> getQuestionnaire2(Integer userId, List<Boolean> answer);
    ScenarioDTO getRecommendedSportPlan(Integer userId, List<Answer2> answer,String token);
}
