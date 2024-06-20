package edu.tongji.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO2 {
    String gender;
    String type;
    Integer age;
    Integer height;
    Integer weight;
    Integer diagnosed_year;
}
