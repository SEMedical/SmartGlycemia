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





import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PatientList {
    String patientId;
    String patientName;
    String patientAvatar = "none";

    String timestamp;

    Integer patientAge;
    // 构造方法

    public PatientList(String rawJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(rawJson);
        patientId = jsonNode.get("patientId").asText();
        patientName=jsonNode.get("patientName").asText();
        patientAvatar=jsonNode.get("patientAvatar").asText();
        if(jsonNode.get("timestamp")==null)
            timestamp=null;
        else
            timestamp=jsonNode.get("timestamp").toString();
        if(jsonNode.get("patientAge")==null)
            patientAge=0;
        else
           patientAge=Integer.valueOf(jsonNode.get("patientAge").toString());
    }
    public String toString(){
        return JSONObject.toJSONString(this);
    }
}
