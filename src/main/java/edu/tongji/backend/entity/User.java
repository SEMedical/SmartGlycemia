package edu.tongji.backend.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @TableId
    private Integer userId;
    private Integer height;
    private String address;
    private String name;
    private String contact;
    private String password;
    private String role;
}
