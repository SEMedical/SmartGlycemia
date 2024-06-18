package edu.tongji.backend.dto;

public class SinglePatientInfo {
    String gender;
    String type;
    Integer age;
    Integer height;
    Integer weight;
    Integer diagnosed_year;

    // 构造方法
    public SinglePatientInfo(String gender, String type, Integer age, Integer height, Integer weight, Integer diagnosed_year) {
        this.gender = gender;
        this.type = type;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.diagnosed_year = diagnosed_year;
    }

    // 可选：添加getter和setter方法
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getDiagnosed_year() {
        return diagnosed_year;
    }

    public void setDiagnosed_year(Integer diagnosed_year) {
        this.diagnosed_year = diagnosed_year;
    }
}
