package edu.tongji.backend.service.impl;

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
