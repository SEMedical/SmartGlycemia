package edu.tongji.backend.dto;

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
        patientId = jsonNode.get("patientId").toString();
        patientName=jsonNode.get("patientName").toString();
        patientAvatar=jsonNode.get("patientAvatar").toString();
        timestamp=jsonNode.get("timestamp").toString();
        patientAge=Integer.valueOf(jsonNode.get("patientAge").toString());
    }
    public String toString(){
        return JSONObject.toJSONString(this);
    }
}
