package edu.tongji.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.entity.Running;

public interface IRunningService extends IService<Running> {
    void updateRunning(Integer exercise,Double Longitude,Double Latitude) throws Exception;

}
