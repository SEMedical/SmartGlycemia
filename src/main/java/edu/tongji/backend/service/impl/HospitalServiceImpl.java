package edu.tongji.backend.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.dto.HospitalDTO;
import edu.tongji.backend.dto.Result;
import edu.tongji.backend.entity.Hospital;
import edu.tongji.backend.mapper.HospitalMapper;
import edu.tongji.backend.service.IHospitalService;
import edu.tongji.backend.util.SystemConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisCommands;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisCommand;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;

import java.util.*;

import static edu.tongji.backend.util.RedisConstants.HOSPITAL_GEO_KEY;
@Slf4j
@Service
public class HospitalServiceImpl extends ServiceImpl<HospitalMapper, Hospital> implements IHospitalService {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    HospitalMapper hospitalMapper;
    @Override
    public Result queryHospital(Integer current, Double x, Double y) {
        if(x==null||y==null) {
            Page<Hospital> page = query().page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
            for (Hospital hospital : page.getRecords()) {
                log.info(hospital.toString());
            }
            return Result.ok(page.getRecords());
        }
        int source=(current-1)*SystemConstants.DEFAULT_PAGE_SIZE;
        int end=current*SystemConstants.DEFAULT_PAGE_SIZE;
        String key=HOSPITAL_GEO_KEY;
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo()
                .search(key, GeoReference.fromCoordinate(x, y),
                        new Distance(5000), RedisGeoCommands.GeoSearchCommandArgs
                                .newGeoSearchArgs().includeDistance().limit(end));
        if(results==null){
            return Result.ok(Collections.emptyList());
        }
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>>
                list=results.getContent();
        //cut between [source,end]
        List<Long> ids=new ArrayList<>(list.size());
        Map<String,Distance> distanceMap=new HashMap<>(list.size());
        list.stream().skip(source).forEach(result->{
            String name = result.getContent().getName();
            if(!"null".equals(name)) {
                ids.add(Long.valueOf(name));
                Distance distance = result.getDistance();
                distanceMap.put(name, distance);
            }
        });
        if(list.size()<=source)
            return Result.ok(Collections.emptyList());
        String idStr = StrUtil.join(",", ids);
        List<Hospital> hospitals = query().in("hospital_id", ids).last("ORDER BY FIELD(hospital_id," + idStr + ")").list();
        for (Hospital hospital : hospitals) {
            log.info(hospital.toString());
        }
        return Result.ok(hospitals);
    }
}
