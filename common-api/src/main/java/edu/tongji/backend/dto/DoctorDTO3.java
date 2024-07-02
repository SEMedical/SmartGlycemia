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
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO3 {
    String user_name;
    String user_group;//Role
    String user_phone;
    String user_id;
    String department;
    String title;
    String hospital_name;
    public DoctorDTO3(Object user_name, Object user_group, Object user_phone, Object user_id, Object hospital_name, Object department, Object title) {
        this.user_name = user_name.toString();
        this.user_group = user_group.toString();
        this.user_phone = user_phone.toString();
        this.user_id = user_id.toString();
        this.hospital_name = hospital_name.toString();
        this.department = department.toString();
        this.title = title.toString();
    }
}
