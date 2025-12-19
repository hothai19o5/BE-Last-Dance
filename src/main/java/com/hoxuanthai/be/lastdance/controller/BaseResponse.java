package com.hoxuanthai.be.lastdance.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {

    private int status; // Mã trạng thái (200, 400, 500...)
    private String message; // Thông báo (Success, Validation Error...)
    private T data; // Dữ liệu chính (Object, List, String...)

    // Phản hồi thành công chỉ có thông báo (VD: Xóa thành công)
    public static <T> ResponseEntity<BaseResponse<T>> success() {
        BaseResponse<T> response = BaseResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .message("Success")
                .build();
        return ResponseEntity.ok(response);
    }

    // Phản hồi thành công kèm dữ liệu (VD: Lấy thông tin user)
    public static <T> ResponseEntity<BaseResponse<T>> success(T data) {
        BaseResponse<T> response = BaseResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .message("Success")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    // Phản hồi thành công kèm dữ liệu và thông báo tùy chỉnh
    public static <T> ResponseEntity<BaseResponse<T>> success(T data, String message) {
        BaseResponse<T> response = BaseResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<BaseResponse<T>> created(T data) {
        BaseResponse<T> response = BaseResponse.<T>builder()
                .status(HttpStatus.CREATED.value()) // 201 Created
                .message("Created successfully")
                .data(data)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public static <T> ResponseEntity<BaseResponse<T>> badRequest(String message) {
        BaseResponse<T> response = BaseResponse.<T>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    public static <T> ResponseEntity<BaseResponse<T>> unauthorized(String message) {
        BaseResponse<T> response = BaseResponse.<T>builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(message)
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    public static <T> ResponseEntity<BaseResponse<T>> forbidden(String message) {
        BaseResponse<T> response = BaseResponse.<T>builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message(message)
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    public static <T> ResponseEntity<BaseResponse<T>> notFound(String message) {
        BaseResponse<T> response = BaseResponse.<T>builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(message)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    public static <T> ResponseEntity<BaseResponse<T>> internalServerError(String message) {
        BaseResponse<T> response = BaseResponse.<T>builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(message)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Phản hồi lỗi (Dùng khi bắt Exception)
    public static <T> ResponseEntity<BaseResponse<T>> error(int status, String message) {
        BaseResponse<T> response = BaseResponse.<T>builder()
                .status(status)
                .message(message)
                .build();
        return ResponseEntity.status(status).body(response);
    }
}