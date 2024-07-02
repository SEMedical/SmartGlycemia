package edu.tongji.backend.mapper;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 Victor Hu,UltraTempest10
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
import edu.tongji.backend.entity.Profile;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProfileMapper extends BaseMapper<Profile> {
    Profile getByPatientIdProfile(Integer patient_id);

    @Update("UPDATE profile SET gender = #{gender}, age = #{age}, weight = #{weight}, height = #{height}, type = #{type}, diagnosed_year = #{diagnosedYear}, family_history = #{familyHistory} WHERE patient_id = #{patientId}")
    boolean update(Profile profile);
    @Select("select age from profile where patient_id= #{id};")
    Integer getUserAge(int id);
}
