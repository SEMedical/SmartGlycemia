package edu.tongji.backend;

import com.baomidou.mybatisplus.annotation.TableId;
import edu.tongji.backend.dto.HospitalDTO;
import edu.tongji.backend.mapper.HospitalMapper;
import edu.tongji.backend.service.impl.HospitalServiceImpl;
import io.lettuce.core.api.sync.RedisGeoCommands;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoLocation;

import java.util.ArrayList;
import java.util.List;

import static edu.tongji.backend.util.RedisConstants.HOSPITAL_GEO_KEY;

@SpringBootTest
public class GEOTest {
    @Autowired
    HospitalMapper hospitalMapper;
    @Autowired
    HospitalServiceImpl hospitalService;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Test
    void loadHospitalData(){
        List<HospitalDTO> list=hospitalMapper.getAllGEO();
        for (HospitalDTO element:list) {
            System.out.println(element.getId());
            stringRedisTemplate.opsForGeo().add(HOSPITAL_GEO_KEY,
                    new Point(element.getLongitude(),element.getLatitude()), element.getId().toString());
        }
    }
    @Test void findNearByHospitals(){
        //The Bund
        hospitalService.queryHospital(1, 121.4880021,31.2402611);
        //Siping Rd.
        hospitalService.queryHospital(1,121.4983751,31.2837);
    }
}
