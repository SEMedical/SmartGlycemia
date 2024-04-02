package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.entity.Running;
import edu.tongji.backend.mapper.RunningMapper;
import edu.tongji.backend.service.IRunningService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static edu.tongji.backend.util.RedisConstants.EXERCISE_RUNNING_KEY;
import static edu.tongji.backend.util.RedisConstants.EXERCISE_RUNNING_TTL;

@Service
public class RunningServiceImpl extends ServiceImpl<RunningMapper, Running> implements IRunningService {
    @Autowired
    RunningMapper runningMapper;
    @Resource
    RedisTemplate stringRedisTemplate;
    @Override
    @Transactional
    public Running updateRunning(Integer exercise_id) {
        Running running=runningMapper.getByExerciseIdRunning(exercise_id);
        Double origin_distance=running.getDistance();
        //随机生成pace和distance
        //pace: 3-15min/km 以秒为单位
        //distance:在origin_distance的基础上增加0到0.02公里 以公里为单位
        Integer pace = (int) (Math.random()*720+180);
        System.out.println("起始路程为"+origin_distance+"公里");
        Double distance = origin_distance+(Math.random()*0.02);
        //根据exercise_id更新
        running.setPace(pace);
        running.setDistance(distance);
        stringRedisTemplate.opsForHash().put(EXERCISE_RUNNING_KEY+exercise_id
                ,"pace",pace.toString());
        stringRedisTemplate.opsForHash().put(EXERCISE_RUNNING_KEY+exercise_id
                ,"distance",distance.toString());
        stringRedisTemplate.expire(EXERCISE_RUNNING_KEY+exercise_id,
                EXERCISE_RUNNING_TTL, TimeUnit.SECONDS);
        QueryWrapper<Running> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("exercise_id",exercise_id);
        runningMapper.update(running,queryWrapper);
        return running;
    }
}
