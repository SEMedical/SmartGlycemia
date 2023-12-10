package com.mybatisplus.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data//自动完成get set函数
@AllArgsConstructor//自动完成有参构造
@NoArgsConstructor//自动完成无参构造
public class user {
    private Integer user_id;//需要与表格里的属性对应
    private Integer age;
    private String name;
    private String contact;
    private String password;
    private String role;
}
