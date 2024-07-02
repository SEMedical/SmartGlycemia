package edu.tongji.backend.service;

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




import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.dto.ProfileDTO;
import edu.tongji.backend.entity.Profile;

import java.text.ParseException;

public interface IProfileService extends IService<Profile> {
    //Profile getCondensedProfile(String user_id);
    Profile getByPatientId(String patient_id);

    ProfileDTO getCompleteProfile(Integer patientId) throws ParseException;

    boolean updateProfile(Integer patientId, ProfileDTO profileDTO) throws ParseException;
    String getUserName(Integer patientId);
    String getContact2(Integer patientId);
    Integer getUserAge(Integer patientId);
}
