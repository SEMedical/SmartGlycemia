package edu.tongji.backend.aspect;

import edu.tongji.backend.dto.Result;
import edu.tongji.backend.exception.GlycemiaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@RestControllerAdvice
@Slf4j
public class ExceptionAspect {
    @ExceptionHandler(GlycemiaException.class)
    @ResponseBody
    public Result handleGlyException(GlycemiaException e) {
        log.error("Expected chained glycemia exception:"+e.getMessage()+e.getCause());
        return Result.fail("Expected chained glycemia exception:"+e.getMessage()+e.getCause());
    }
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handleOtherException(Exception e) {
        log.error("unexpected exception", e);
        return Result.fail("Unexpected exception occurred!");
    }
}
