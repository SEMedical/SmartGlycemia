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
