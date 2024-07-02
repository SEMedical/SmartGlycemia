package edu.tongji.backend.dto;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 Victor Hu
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




import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
    private String gender;
    private Integer age;
    private String height;

    public ProfileDTO(String gender, Integer age, String height, String weight, String diabetesType, String complications, Integer diagnosisYear, String familyHistory) {
        this.gender = gender;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.diabetesType = diabetesType;
        this.complications = complications;
        this.diagnosisYear = diagnosisYear;
        this.familyHistory = familyHistory;
    }

    private String weight;
    private String diabetesType;
    private String complications;
    private Integer diagnosisYear;
    private String familyHistory;
    private String name;
    private String contact;
}
