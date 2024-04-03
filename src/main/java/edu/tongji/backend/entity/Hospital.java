package edu.tongji.backend.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hospital {
    @TableId
    Long hospital_id;
    String name;
    Double Latitude;
    Double Longitude;
    String address;
    String contact;
}
