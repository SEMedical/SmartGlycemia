package edu.tongji.backend.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private boolean isSuccess;
    private T result;
    private String msg;

    public static <T> Result<T> success(T res, String message) {
        return new Result<>(true, res, message);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(false, null ,message);
    }
}
