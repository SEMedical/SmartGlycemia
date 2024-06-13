package edu.tongji.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorInfoDTO {
    private Integer doctorId;
    private String idCard;
    private String department;
    private String title;
    private String photoPath;

    private String name;
    private String contact;
    private String state;
}
