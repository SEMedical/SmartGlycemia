package edu.tongji.backend.mapper;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 Victor Hu
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




import edu.tongji.backend.dto.DoctorDTO2;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PatientInteractMapper {
    //已被包含在下面的方法中
    List<DoctorDTO2> searchName(String keyword);

    //查询医生

    List<DoctorDTO2> searchAll(String keyword);

    //按科室预约医生
    //void appointDoctor(String department, String datetime, int hospitalId);
}
