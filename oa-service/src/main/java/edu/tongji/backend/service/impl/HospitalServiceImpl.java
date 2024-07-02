package edu.tongji.backend.service.impl;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 Victor Hu,rmEleven
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
import edu.tongji.backend.entity.Hospital;
import edu.tongji.backend.mapper.HospitalMapper;
import edu.tongji.backend.service.IHospitalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
public class HospitalServiceImpl extends ServiceImpl<HospitalMapper, Hospital> implements IHospitalService {
    @Autowired
    HospitalMapper hospitalMapper;
    static Integer id;
    @Override
    public Integer addHospital(Hospital hospital) {
        try {
            id=hospitalMapper.getMaxId();
            synchronized (id) {
                hospital.setHospitalId(id + 1);
            }
            hospitalMapper.insert(hospital);
        }catch (Exception e){
            System.err.println(e.getMessage());
            throw e;
        }
        return id;
    }

    @Override
    public void deleteHospital(int hospitalId) {
        int i = hospitalMapper.deleteById(hospitalId);
        System.out.println(i);
        if(i==0)
            throw new NoSuchElementException("The Hospital "+hospitalId+" doesn't exist or has been removed earlier!");
    }
}
