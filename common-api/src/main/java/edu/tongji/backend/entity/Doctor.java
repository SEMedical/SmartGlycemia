package edu.tongji.backend.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {
    @TableId
    private Integer doctorId;

    private int hospitalId;
    private String idCard;
    private String department;
    private String title;
    private String photoPath;
    private String state;
}
