package edu.tongji.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.dto.Sport;
import edu.tongji.backend.entity.Scenario;

public interface IScenarioService extends IService<Scenario> {
    boolean setScenario(Integer patientId, Sport sport);
}
