package edu.tongji.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorEditDTO {
    Integer doctorId;
    String name;
    String idCard;
    String department;
    String title;
    String contact;
    String photoPath;
    String state;
}
