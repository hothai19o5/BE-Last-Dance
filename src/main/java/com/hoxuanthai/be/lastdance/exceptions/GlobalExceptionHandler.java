package com.hoxuanthai.be.lastdance.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
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

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on November 2025
 *
 * @author HoXuanThai
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Xử lý ngoại lệ NoHandlerFoundException, xảy ra khi không tìm thấy handler cho một request.
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
		final ApiExceptionResponse response = new ApiExceptionResponse("Resource not found", HttpStatus.NOT_FOUND, LocalDateTime.now());
		return ResponseEntity.status(response.getStatus()).body(response);
	}

    /**
     * Xử lý ngoại lệ HttpRequestMethodNotSupportedException, xảy ra khi request sử dụng một phương thức HTTP không được hỗ trợ (ví dụ: GET thay vì POST).
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
		final ApiExceptionResponse response = new ApiExceptionResponse("Method not supported", HttpStatus.METHOD_NOT_ALLOWED, LocalDateTime.now());
		return ResponseEntity.status(response.getStatus()).body(response);
	}

    /**
     * Xử lý ngoại lệ HttpMediaTypeNotSupportedException, xảy ra khi request có kiểu media không được hỗ trợ.
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
		final ApiExceptionResponse response = new ApiExceptionResponse("Media type not supported", HttpStatus.UNSUPPORTED_MEDIA_TYPE, LocalDateTime.now());
		return ResponseEntity.status(response.getStatus()).body(response);
	}

    /**
     * Xử lý ngoại lệ MissingServletRequestParameterException, xảy ra khi thiếu một tham số bắt buộc trong request.
     *
     * @param ex      Ngoại lệ MissingServletRequestParameterException.
     * @param headers Headers của HTTP response.
     * @param status  Trạng thái HTTP.
     * @param request WebRequest hiện tại.
     * @return ResponseEntity chứa thông tin lỗi.
     */
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
			MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		final ApiExceptionResponse response = new ApiExceptionResponse(ex.getParameterName() + " parameter is missing", HttpStatus.BAD_REQUEST, LocalDateTime.now());
		return ResponseEntity.status(response.getStatus()).body(response);
	}

    /**
     * Xử lý ngoại lệ MethodArgumentNotValidException, xảy ra khi validation cho một đối số được đánh dấu @Valid thất bại.
     * Ghi đè (Override) phương thức từ ResponseEntityExceptionHandler để tránh lỗi 'Ambiguous @ExceptionHandler'.
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
            WebRequest request
    ) {
        final List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        final List<String> errorList = fieldErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();

        // Giả sử ValidationErrorResponse được định nghĩa ở đâu đó
        // (Nếu không, bạn có thể tạo một Map<String, Object> như ví dụ trước)
        final ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse(HttpStatus.BAD_REQUEST, LocalDateTime.now(), errorList);

        log.warn("Validation errors : {} , Parameters : {}", errorList, ex.getBindingResult().getTarget());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrorResponse);
    }

    /**
     * Xử lý ngoại lệ tùy chỉnh RegistrationException, liên quan đến lỗi trong quá trình đăng ký người dùng.
     *
     * @param exception Ngoại lệ RegistrationException.
     * @return ResponseEntity chứa thông tin lỗi.
     */
	@ExceptionHandler(RegistrationException.class)
	ResponseEntity<ApiExceptionResponse> handleRegistrationException(RegistrationException exception) {

		final ApiExceptionResponse response = new ApiExceptionResponse(exception.getErrorMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());

		return ResponseEntity.status(response.getStatus()).body(response);
	}

    /**
     * Xử lý ngoại lệ BadCredentialsException, xảy ra khi xác thực thất bại (ví dụ: sai tên người dùng hoặc mật khẩu).
     *
     * @param exception Ngoại lệ BadCredentialsException.
     * @return ResponseEntity chứa thông tin lỗi.
     */
	@ExceptionHandler(BadCredentialsException.class)
	ResponseEntity<ApiExceptionResponse> handleBadCredentialsException(BadCredentialsException exception) {

		final ApiExceptionResponse response = new ApiExceptionResponse(exception.getMessage(), HttpStatus.UNAUTHORIZED, LocalDateTime.now());

		return ResponseEntity.status(response.getStatus()).body(response);
	}

    /**
     * Xử lý ngoại lệ AccessDeniedException, xảy ra khi một người dùng đã được xác thực cố gắng truy cập một tài nguyên mà họ không có quyền.
     *
     * @param ex      Ngoại lệ AccessDeniedException.
     * @param request WebRequest hiện tại.
     * @return ResponseEntity chứa thông tin lỗi.
     */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiExceptionResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
		final ApiExceptionResponse response = new ApiExceptionResponse("Access Denied", HttpStatus.FORBIDDEN, LocalDateTime.now());
		return ResponseEntity.status(response.getStatus()).body(response);
	}

    /**
     * Xử lý tất cả các ngoại lệ khác không được xử lý cụ thể. Đây là một trình xử lý chung.
     *
     * @param ex      Ngoại lệ chung.
     * @param request WebRequest hiện tại.
     * @return ResponseEntity chứa thông báo lỗi chung.
     */
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ApiExceptionResponse> handleAllExceptions(Exception ex, WebRequest request) {
		log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
		final ApiExceptionResponse response = new ApiExceptionResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now());
		return ResponseEntity.status(response.getStatus()).body(response);
	}
}
