package edu.tongji.backend.util;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import com.alibaba.druid.util.StringUtils;
import com.ctc.wstx.util.StringUtil;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
@Component
public class HeaderOriginParser implements RequestOriginParser {
    @Override
    public String parseOrigin(HttpServletRequest request) {
        String origin=request.getHeader("origin");
        if(StringUtils.isEmpty(origin)){
            return "blank";
        }
        return origin;
    }
}
