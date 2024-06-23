package edu.tongji.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDTO1 {
    int hospital_id;
    String name;
    String id_card;
    String department;
    String title;
    String photo_path;
    String contact;
}
