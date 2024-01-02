package edu.tongji.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Running {
    private int exerciseId;
    private int pace;
    private double distance;
}
