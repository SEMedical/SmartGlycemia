package edu.tongji.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.entity.Running;

public interface IRunningService extends IService<Running> {
    Running updateRunning(Integer exercise_id);

}
