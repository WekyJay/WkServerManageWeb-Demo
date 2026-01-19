package cn.wekyjay.demo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 标准 RESTful 响应封装类。
 * - success: 是否成功
 * - code: 业务/HTTP风格的状态码（默认成功为 0）
 * - message: 描述信息
 * - data: 业务数据
 * - timestamp: 响应时间戳（毫秒）
 */
public class Result<T> {

    public static final int CODE_OK = 0;
    public static final int CODE_BAD_REQUEST = 400;
    public static final int CODE_UNAUTHORIZED = 401;
    public static final int CODE_FORBIDDEN = 403;
    public static final int CODE_NOT_FOUND = 404;
    public static final int CODE_ERROR = 500;

    private final boolean success;
    private final int code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    private final long timestamp;

    private Result(boolean success, int code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public boolean isSuccess() {
        return success;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // -------- 静态工厂方法（成功） --------
    public static <T> Result<T> ok() {
        return new Result<>(true, CODE_OK, "OK", null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(true, CODE_OK, "OK", data);
    }

    public static <T> Result<T> ok(String message, T data) {
        return new Result<>(true, CODE_OK, message, data);
    }

    // -------- 静态工厂方法（失败） --------
    public static <T> Result<T> fail(String message) {
        return new Result<>(false, CODE_BAD_REQUEST, message, null);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(false, code, message, null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(false, CODE_ERROR, message, null);
    }

    public static <T> Result<T> error(Throwable e) {
        String msg = e == null ? "Unknown error" : e.getMessage();
        return new Result<>(false, CODE_ERROR, msg, null);
    }

    // -------- 通用构造 --------
    public static <T> Result<T> of(boolean success, int code, String message, T data) {
        return new Result<>(success, code, message, data);
    }
}