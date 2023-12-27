package edu.tongji.backend.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chart {
    Integer error_code;
    List<Map<LocalDateTime,Double>> data;
}
