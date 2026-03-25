package com.esther.fluxsync.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(int code, String message, T data) {

    // 200 + success message
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "操作成功", null);
    }

    // 200 + success message + custom data
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "操作成功", data);
    }

    // 200 + custom message + custom data
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    // error http-code + custom message
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    // error http-code + custom message + custom data
    public static <T> ApiResponse<T> error(int code, String message, T data) { return new ApiResponse<>(code, message, data); }

    // 500 + custom message
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null);
    }

}
