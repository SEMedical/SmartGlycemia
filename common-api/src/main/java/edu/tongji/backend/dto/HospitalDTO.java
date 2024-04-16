package edu.tongji.backend.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HospitalDTO {
    Long id;
    Double Latitude;
    Double Longitude;
}
