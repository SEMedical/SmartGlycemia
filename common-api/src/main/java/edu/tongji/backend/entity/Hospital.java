package edu.tongji.backend.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hospital {
    @TableId
    private Integer hospitalId;

    private String hospitalName;
    private String level;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String zipcode;
    private String hospitalPhone;
    private String outpatientHours;
    private String introduction;
}
