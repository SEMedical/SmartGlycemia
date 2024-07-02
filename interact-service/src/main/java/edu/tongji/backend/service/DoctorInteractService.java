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




import com.fasterxml.jackson.core.JsonProcessingException;
import edu.tongji.backend.dto.DoctorDTO3;
import edu.tongji.backend.dto.PatientList;
import edu.tongji.backend.dto.SinglePatientInfo;
import edu.tongji.backend.dto.applyList;

import java.util.List;

public interface DoctorInteractService {
//    获取患者列表
    PatientList[] getPatientList() throws JsonProcessingException;
//    医生获取单个患者信息
    SinglePatientInfo getSinglePatientInfo(String patientId);
//   医生确认患者申请
    Boolean confirmPatient(String messageId, String doctor_id) throws NullPointerException,IllegalArgumentException;

    //   医生获取申请列表
    applyList[] doctorGetApplicationList(String doctorId);

    Integer getFollowersNum(String doctor_id);

    Integer getFolloweesNum(String userId);

    List<PatientList> getFollowerList(String doctor_id) throws JsonProcessingException;

    Boolean discardPatient(String messageId, String userId);

    DoctorDTO3 getDoctorInfo(String doctor_id);
}
