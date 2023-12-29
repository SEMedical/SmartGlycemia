package edu.tongji.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RegisterDTO {
    private String name;
    private String password;
    private String contact;
    private String gender;
    private Integer age;
}
