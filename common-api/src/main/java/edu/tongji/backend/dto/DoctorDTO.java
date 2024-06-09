package edu.tongji.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDTO {
    int hospital_id;
    String id_card;
    String department;
    String title;
    String photo_path;
    String contact;
}
