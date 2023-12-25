package edu.tongji.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    @Basic
    @Column(name = "height")
    private Integer height;
    @Basic
    @Column(name = "address", length = 100)
    private String address;
    @Basic
    @Column(name = "name", length = 45)
    private String name;
    @Basic
    @Column(name = "contact", length = 11)
    private String contact;
    @Basic
    @Column(name = "password", length = 64)
    private String password;
    @Basic
    @Column(name = "role", length = 10)
    private String role;
}
