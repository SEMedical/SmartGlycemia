package edu.tongji.backend.service;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 a-little-dust
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
