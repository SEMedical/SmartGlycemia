package edu.tongji.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tip {
    private String tip;
    //RGBA combined as Color
    private MyColor color;
}
