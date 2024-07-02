package edu.tongji.backend.dto;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 All contributors of the project
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */





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
        if(age==null)
            return -1;
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getHeight() {
        if(height==null)
            height=-1;
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        if(weight==null)
            return -1;
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getDiagnosed_year() {
        if(diagnosed_year==null)
            return 1970;
        return diagnosed_year;
    }

    public void setDiagnosed_year(Integer diagnosed_year) {
        this.diagnosed_year = diagnosed_year;
    }
}
