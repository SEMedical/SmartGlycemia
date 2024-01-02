package edu.tongji.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Scenario {
    public enum Category{
        running,jogging,yoga,ropeskipping
    }
    public static String check(String category) {
        for(int i = 0; i < Category.values().length; i++) {
            if(Category.values()[i].toString().equals(category.toLowerCase())) {
                return category;
            }
        }
        return null;
    }
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "patient_id", nullable = false)
    private int patientId;


    @Basic
    @Column(name = "category", length = 30)
    private String category;


    @Basic
    @Column(name = "duration")
    private Integer duration;


    @Basic
    @Column(name = "calories")
    private Integer calories;
}
