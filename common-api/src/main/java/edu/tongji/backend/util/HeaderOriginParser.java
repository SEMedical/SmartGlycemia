package edu.tongji.backend.util;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import com.ctc.wstx.util.StringUtil;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
@Component
public class HeaderOriginParser implements RequestOriginParser {
    @Override
    public String parseOrigin(HttpServletRequest request) {
        String origin=request.getHeader("origin");
        if(origin==null||origin.equals("")||origin.length()==0){
            return "blank";
        }
        return origin;
    }
}
