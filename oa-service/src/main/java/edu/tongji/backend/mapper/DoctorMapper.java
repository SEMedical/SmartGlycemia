package edu.tongji.backend.mapper;

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





import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.backend.dto.DoctorInfoDTO;
import edu.tongji.backend.entity.Doctor;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {
    @Select("SELECT d.doctor_id, d.id_card, d.department, d.title, d.photo_path, u.name, u.contact, d.state " +
            "FROM doctor d " +
            "JOIN user u ON d.doctor_id = u.user_id;")
    List<DoctorInfoDTO> getAccountList();
    @Update("UPDATE doctor SET id_card= #{idCard},department=#{department},title=#{title},photo_path=#{photoPath}" +
            " where doctor_id=#{doctorId};")
    Boolean updateDoctor(Integer doctorId,String idCard,String department,String title,String photoPath);
    @Select("SELECT count(*)>0 from doctor where id_card= #{ idCard };")
    Boolean repeatedIdCard(String idCard);

//    @Insert("INSERT INTO doctor(doctor_id, hospital_id, id_card, department, title, photo_path)" +
//            "VALUES(#{doctorId}, #{hospitalId}, #{idCard}, #{department}, #{title}, #{photoPath});")
//    void addAccount(Doctor doctor);
//
//    @Delete("DELETE FROM doctor WHERE doctor_id = #{doctorId};")
//    void deleteAccount(int doctorId);
}
