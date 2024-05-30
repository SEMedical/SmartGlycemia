package edu.tongji.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tongji.backend.entity.Complication;

import java.util.ArrayList;
import java.util.List;

public interface IComplicationService extends IService<Complication> {
    static StringBuilder getComplicationStr(List<String> complications) {
        // 'diabetic foot', 'diabetic eye', 'diabetic kidney', 'diabetic cardiovascular disease', ' diabetic neuropathy', 'diabetic skin disease', 'hypertension', 'hyperlipidemia', 'others'
        StringBuilder complicationStr = new StringBuilder();
        for (String complication : complications) {
            if(complication.contains("diabetic foot"))
                complicationStr.append("糖尿病足、");
            if(complication.contains("diabetic eye"))
                complicationStr.append("糖尿病眼、");
            if(complication.contains("diabetic kidney"))
                complicationStr.append("糖尿病肾、");
            if(complication.contains("diabetic cardiovascular disease"))
                complicationStr.append("糖尿病心血管疾病、");
            if(complication.contains("diabetic neuropathy"))
                complicationStr.append("糖尿病神经病变、");
            if(complication.contains("diabetic skin disease"))
                complicationStr.append("糖尿病皮肤病、");
            if(complication.contains("hypertension"))
                complicationStr.append("高血压、");
            if(complication.contains("hyperlipidemia"))
                complicationStr.append("高血脂、");
            if(complication.contains("others"))
                complicationStr.append("其他、");
        }
        return complicationStr;
    }

    static List<String> parseComplicationStr(String complicationStr) {
        List<String> complications = new ArrayList<>();
        if (complicationStr.contains("糖尿病足")) {
            complications.add("diabetic foot");
        }
        if (complicationStr.contains("糖尿病眼")) {
            complications.add("diabetic eye");
        }
        if (complicationStr.contains("糖尿病肾")) {
            complications.add("diabetic kidney");
        }
        if (complicationStr.contains("糖尿病心血管疾病")) {
            complications.add("diabetic cardiovascular disease");
        }
        if (complicationStr.contains("糖尿病神经病变")) {
            complications.add("diabetic neuropathy");
        }
        if (complicationStr.contains("糖尿病皮肤病")) {
            complications.add("diabetic skin disease");
        }
        if (complicationStr.contains("高血压")) {
            complications.add("hypertension");
        }
        if (complicationStr.contains("高血脂")) {
            complications.add("hyperlipidemia");
        }
        complications.add("others");
        return complications;
    }
}
