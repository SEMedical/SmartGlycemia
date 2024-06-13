package edu.tongji.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDTO {
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
    int hospital_id;
    String id_card;
    String department;
    String title;
    String photo_path;
    String contact;
}
