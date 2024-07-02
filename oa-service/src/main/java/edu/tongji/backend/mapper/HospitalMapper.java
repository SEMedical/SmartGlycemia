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




import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.backend.entity.Hospital;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface HospitalMapper extends BaseMapper<Hospital> {
    @Select("SELECT MAX(hospital_id) FROM hospital;")
    Integer getMaxId();
    @Select("SELECT COUNT(*)>=1 from hospital where hospital_id=#{hospital_id}")
    Boolean ValidHospitalId(String hospital_id);
    @Select("SELECT EXISTS(SELECT * from hospital where hospital_phone=CONCAT(\"\",#{ contact } ,\"\")" +
            "or hospital_name=CONCAT(\"\", #{ name },\"\") or address=CONCAT(\"\",#{ address },\"\") )")
    Boolean InfoRepeated(String contact,String name,String address);
    @Select("SELECT admin_id from hospital where hospital_id=#{hospitalId};")
    String havaAdministrator(String hospitalId);
    @Update("UPDATE hospital set admin_id=#{adminId} where hospital_id=#{hospitalId};")
    Boolean setAdministrator(String hospitalId,String adminId);
}
