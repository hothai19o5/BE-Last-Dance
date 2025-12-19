package com.hoxuanthai.be.lastdance.exceptions;

import com.hoxuanthai.be.lastdance.controller.BaseResponse;
import com.hoxuanthai.be.lastdance.ratelimit.RateLimitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Xử lý ngoại lệ NoHandlerFoundException, xảy ra khi không tìm thấy handler cho
     * một request.
     * Thường là lỗi 404 Not Found.
     *
     * @param ex      Ngoại lệ NoHandlerFoundException.
     * @param headers Headers của HTTP response.
     * @param status  Trạng thái HTTP.
     * @param request WebRequest hiện tại.
     * @return ResponseEntity chứa thông tin lỗi.
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        BaseResponse<List<String>> response = BaseResponse.<List<String>>builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("Resource not found")
                .build();
        log.warn("No handler found for request: {} {}", ex.getHttpMethod(), ex.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Xử lý ngoại lệ HttpRequestMethodNotSupportedException, xảy ra khi request sử
     * dụng một phương thức HTTP không được hỗ trợ (ví dụ: GET thay vì POST).
     *
     * @param ex      Ngoại lệ HttpRequestMethodNotSupportedException.
     * @param headers Headers của HTTP response.
     * @param status  Trạng thái HTTP.
     * @param request WebRequest hiện tại.
     * @return ResponseEntity chứa thông tin lỗi.
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        BaseResponse<List<String>> response = BaseResponse.<List<String>>builder()
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .message("Method not supported")
                .build();
        log.warn("Request method not supported: {}", ex.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    /**
     * Xử lý ngoại lệ HttpMediaTypeNotSupportedException, xảy ra khi request có kiểu
     * media không được hỗ trợ.
     *
     * @param ex      Ngoại lệ HttpMediaTypeNotSupportedException.
     * @param headers Headers của HTTP response.
     * @param status  Trạng thái HTTP.
     * @param request WebRequest hiện tại.
     * @return ResponseEntity chứa thông tin lỗi.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        BaseResponse<List<String>> response = BaseResponse.<List<String>>builder()
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .message("Media type not supported: " + ex.getContentType())
                .build();
        log.warn("Media type not supported: {}", ex.getContentType());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    /**
     * Xử lý ngoại lệ MissingServletRequestParameterException, xảy ra khi thiếu một
     * tham số bắt buộc trong request.
     *
     * @param ex      Ngoại lệ MissingServletRequestParameterException.
     * @param headers Headers của HTTP response.
     * @param status  Trạng thái HTTP.
     * @param request WebRequest hiện tại.
     * @return ResponseEntity chứa thông tin lỗi.
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status,
            WebRequest request) {
        BaseResponse<List<String>> response = BaseResponse.<List<String>>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Missing request parameter: " + ex.getParameterName())
                .build();
        log.warn("Missing request parameter: {}", ex.getParameterName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Xử lý ngoại lệ MethodArgumentNotValidException, xảy ra khi validation cho một
     * đối số được đánh dấu @Valid thất bại.
     * Ghi đè (Override) phương thức từ ResponseEntityExceptionHandler để tránh lỗi
     * 'Ambiguous @ExceptionHandler'.
     *
     * @param ex      Ngoại lệ MethodArgumentNotValidException.
     * @param headers Headers của HTTP response.
     * @param status  Trạng thái HTTP.
     * @param request WebRequest hiện tại.
     * @return ResponseEntity chứa danh sách các lỗi validation.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        final List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        final List<String> errorList = fieldErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        BaseResponse<List<String>> response = BaseResponse.<List<String>>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation Error")
                .data(errorList)
                .build();

        log.warn("Validation errors: {}", errorList);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Xử lý ngoại lệ tùy chỉnh RegistrationException, liên quan đến lỗi trong quá
     * trình đăng ký người dùng.
     *
     * @param exception Ngoại lệ RegistrationException.
     * @return ResponseEntity chứa thông tin lỗi.
     */
    @ExceptionHandler(RegistrationException.class)
    ResponseEntity<BaseResponse<Void>> handleRegistrationException(RegistrationException exception) {
        log.warn("Registration error: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.<Void>builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(exception.getMessage())
                        .build());
    }

    /**
     * Xử lý ngoại lệ BadCredentialsException, xảy ra khi xác thực thất bại (ví dụ:
     * sai tên người dùng hoặc mật khẩu).
     *
     * @param exception Ngoại lệ BadCredentialsException.
     * @return ResponseEntity chứa thông tin lỗi.
     */
    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<BaseResponse<Void>> handleBadCredentialsException(BadCredentialsException exception) {
        log.warn("Authentication failed: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(BaseResponse.<Void>builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message("Invalid username or password")
                        .build());
    }

    /**
     * Xử lý ngoại lệ DisabledException, xảy ra khi tài khoản người dùng bị vô hiệu hóa.
     *
     * @param exception Ngoại lệ DisabledException.
     * @return ResponseEntity chứa thông tin lỗi.
     */
    @ExceptionHandler(DisabledException.class)
    ResponseEntity<BaseResponse<Void>> handleDisabledException(DisabledException exception) {
        log.warn("Account disabled: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(BaseResponse.<Void>builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message("User account is disabled!")
                        .build());
    }

    /**
     * Xử lý ngoại lệ AccessDeniedException, xảy ra khi một người dùng đã được xác
     * thực cố gắng truy cập một tài nguyên mà họ không có quyền.
     *
     * @param ex      Ngoại lệ AccessDeniedException.
     * @param request WebRequest hiện tại.
     * @return ResponseEntity chứa thông tin lỗi.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Void>> handleAccessDeniedException(AccessDeniedException ex,
            WebRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.<Void>builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .message("You do not have permission to access this resource.")
                        .build());
    }

    /**
     * Xử lý ngoại lệ tùy chỉnh ResourceNotFoundException, xảy ra khi một tài nguyên
     * không được tìm thấy.
     * @param ex      Ngoại lệ ResourceNotFoundException.
     * @param request WebRequest hiện tại.
     * @return ResponseEntity chứa thông tin lỗi.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex,
            WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.<Void>builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message(ex.getMessage())
                        .build());
    }

    /**
     * Xử lý ngoại lệ RateLimitException, xảy ra khi vượt quá giới hạn request.
     *
     * @param ex Ngoại lệ RateLimitException.
     * @return ResponseEntity chứa thông tin lỗi.
     */
    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<BaseResponse<Void>> handleRateLimitException(RateLimitException ex) {
        log.warn("Rate limit exceeded: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", String.valueOf(ex.getRetryAfterSeconds()))
                .body(BaseResponse.<Void>builder()
                        .status(HttpStatus.TOO_MANY_REQUESTS.value())
                        .message("Too many requests. Please try again after " + ex.getRetryAfterSeconds() + " seconds.")
                        .build());
    }

    /**
     * Xử lý tất cả các ngoại lệ khác không được xử lý cụ thể. Đây là một trình xử
     * lý chung.
     *
     * @param ex      Ngoại lệ chung.
     * @param request WebRequest hiện tại.
     * @return ResponseEntity chứa thông báo lỗi chung.
     */
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<BaseResponse<Void>> handleAllExceptions(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.<Void>builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("An unexpected error occurred. Please try again later.")
                        .build());
    }
}