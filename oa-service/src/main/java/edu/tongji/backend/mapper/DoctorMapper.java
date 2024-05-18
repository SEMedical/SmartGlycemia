package edu.tongji.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.backend.dto.DoctorInfoDTO;
import edu.tongji.backend.entity.Doctor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {
    @Select("SELECT d.doctor_id, d.hospital_id, d.id_card, d.department, d.title, d.photo_path, u.address, u.name, u.contact, u.height " +
            "FROM doctor d " +
            "JOIN user u ON d.doctor_id = u.user_id;")
    List<DoctorInfoDTO> getAccountList();

//    @Insert("INSERT INTO doctor(doctor_id, hospital_id, id_card, department, title, photo_path)" +
//            "VALUES(#{doctorId}, #{hospitalId}, #{idCard}, #{department}, #{title}, #{photoPath});")
//    void addAccount(Doctor doctor);
//
//    @Delete("DELETE FROM doctor WHERE doctor_id = #{doctorId};")
//    void deleteAccount(int doctorId);
}
