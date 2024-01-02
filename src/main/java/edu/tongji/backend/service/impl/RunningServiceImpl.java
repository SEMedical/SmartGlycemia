package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.entity.Running;
import edu.tongji.backend.mapper.RunningMapper;
import edu.tongji.backend.service.IRunningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RunningServiceImpl extends ServiceImpl<RunningMapper, Running> implements IRunningService {
    @Autowired
    RunningMapper runningMapper;
    @Override
    public void updateRunning(int exercise_id) {
        Running running=runningMapper.selectById(exercise_id);
        double origin_distance=running.getDistance();
        //随机生成pace和distance
        //pace: 3-15min/km 以秒为单位
        //distance:在origin_distance的基础上增加0到1公里 以公里为单位
        int pace = (int)(Math.random()*720+180);
        double distance = origin_distance+Math.random()*1;
        //根据exercise_id更新
        running.setPace(pace);
        running.setDistance(distance);
        runningMapper.updateById(running);
    }
}
