package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.entity.Scenario;
import edu.tongji.backend.mapper.ScenarioMapper;
import edu.tongji.backend.service.IScenarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScenarioServiceImpl extends ServiceImpl<ScenarioMapper, Scenario> implements IScenarioService {
    @Autowired
    ScenarioMapper scenarioMapper;
}
