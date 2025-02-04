package com.qvl.gethomeweb.util;

import lombok.Data;
import java.util.HashMap;

@Data
public class Result<T> extends HashMap<String, Object> {

    private static final String SUCCESS = "200";
    private static final String ERROR = "500";

    private Result() {
    }

    private Result(String code, String msg) {
        super.put("code", code);
        super.put("msg", msg);
    }

    private Result(String code, String msg, T data) {
        super.put("code", code);
        super.put("msg", msg);
        super.put("data", data);
    }

    public static Result success() {
        return new Result<>(SUCCESS, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS, null, data);
    }

    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(SUCCESS, msg, data);
    }

    public static <T> Result<T> success(String code, String msg, T data) {
        return new Result<>(code, msg, data);
    }

    public static Result error() {
        return new Result<>(ERROR, null);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(ERROR, msg, null);
    }

    public static <T> Result<T> error(String code, String msg) {
        return new Result<>(code, msg, null);
    }

    @Override
    public Result put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
