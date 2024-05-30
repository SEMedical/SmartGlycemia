package edu.tongji.backend.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {
    private String name;
    private String contact;
    private String avatar;
    private String title;
    private String department;
    private int hospital_id;
    private int doctor_id;
    private String hospital_name;
    private String address;
    private String introduction;
}
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//class DoctorBackup {
//    @TableId
//    private Integer doctorId;
//
//    private int hospitalId;
//    private String idCard;
//    private String department;
//    private String title;
//    private String photoPath;
//}
