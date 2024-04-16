package edu.tongji.backend.exception;

public class GlycemiaException extends RuntimeException {

    // 添加自定义的构造方法
    public GlycemiaException() {
        super();
    }

    public GlycemiaException(String message) {
        super("Expected glycemia exception:"+message);
    }

    public GlycemiaException(String message, Throwable cause) {
        super("Expected chained glycemia exception:"+message, cause);
    }

    public GlycemiaException(Throwable cause) {
        super("Expected chained glycemia exception:"+cause);
    }
}
