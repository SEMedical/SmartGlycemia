package edu.tongji.backend.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> {
    private boolean isSuccess;
    private T response;
    private String message;

    public static <T> Response<T> success(T res, String msg) {
        return new Response<>(true, res, msg);
    }

    public static <T> Response<T> fail(String msg) {
        return new Response<>(false, null ,msg);
    }
}
