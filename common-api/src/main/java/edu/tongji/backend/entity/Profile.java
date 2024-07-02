package edu.tongji.backend.entity;

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




import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    @TableId
    private int patientId;

    private String gender;

    private String type;

    private Integer age;

    private Integer height;

    private Integer weight;

    private String familyHistory;

    private String diagnosedYear;
    private String anamnesis;

    private String medicationPattern;

    private String allergy;

    private String medicationHistory;
    private Boolean dietaryTherapy;
    private Boolean exerciseTherapy;
    private Boolean oralTherapy;
    private Boolean insulinTherapy;
}
