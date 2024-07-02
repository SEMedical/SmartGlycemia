package edu.tongji.backend.service.impl;

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





import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.dto.Sport;
import edu.tongji.backend.entity.Profile;
import edu.tongji.backend.entity.Scenario;
import edu.tongji.backend.mapper.ScenarioMapper;
import edu.tongji.backend.service.IScenarioService;
import edu.tongji.backend.util.CalorieCalculator;
import edu.tongji.backend.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
@Slf4j
@Service
public class ScenarioServiceImpl extends ServiceImpl<ScenarioMapper, Scenario> implements IScenarioService {
    @Autowired
    ScenarioMapper scenarioMapper;
    @Resource
    RestTemplate restTemplate;
    @Override
    public boolean setScenario(Integer patientId, Sport sport,String token) {
        log.info("种类为"+ sport.getCategory() );
        Scenario scenario = new Scenario();
        scenario.setPatientId(patientId);
        scenario.setCategory(sport.getCategory());
        scenario.setDuration(sport.getDuration());
        int weight=70;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", token);
        HttpEntity entity = new HttpEntity<>(headers);
        String url = "http://localhost:81/api/health/getProfile?patient_id="+patientId;
        // 2.2.发送http请求，实现远程调用
        Response<Profile> profile0 = restTemplate.getForObject(url, Response.class);
        Profile profile=profile0.getResponse();
        if (profile != null && profile.getWeight() != null&&profile.getWeight()>0) {
            weight = profile.getWeight();
            log.info("weight: " + weight);
        }

        Integer calories = CalorieCalculator.getCalorie(sport.getCategory(), sport.getDuration(), weight);

        scenario.setCalories(calories);

        if (scenarioMapper.getByPatientId(patientId) != null) {
            return scenarioMapper.update(scenario);
        } else {
            return scenarioMapper.insert(scenario) == 1;
        }
    }
}
