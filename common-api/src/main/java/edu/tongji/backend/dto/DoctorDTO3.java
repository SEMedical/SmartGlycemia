package edu.tongji.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO3 {
    String user_name;
    String user_group;//Role
    String user_phone;
    String user_id;
    String department;
    String title;
    String hospital_name;
    public DoctorDTO3(Object user_name, Object user_group, Object user_phone, Object user_id, Object hospital_name, Object department, Object title) {
        this.user_name = user_name.toString();
        this.user_group = user_group.toString();
        this.user_phone = user_phone.toString();
        this.user_id = user_id.toString();
        this.hospital_name = hospital_name.toString();
        this.department = department.toString();
        this.title = title.toString();
    }
}
