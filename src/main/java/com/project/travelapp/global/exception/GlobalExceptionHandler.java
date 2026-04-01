package com.project.travelapp.global.exception;

import com.project.travelapp.global.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 비즈니스 로직 에러 (우리가 서비스에서 던진 IllegalArgumentException 처리)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.success(400, ex.getMessage(), null));
    }

    // 2. 입력값 검증 에러 (DTO의 @Valid, @NotBlank 등에서 걸렸을 때 처리)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.success(400, "입력값이 올바르지 않습니다.", errors));
    }

    // 3. 그 외 서버에서 터진 모든 에러 (최후의 보루)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllExceptions(Exception ex) {
        // 실무에서는 여기서 에러 로그(ex.getMessage(), ex.printStackTrace())를 슬랙 등으로 전송합니다.
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.success(500, "서버 내부 오류가 발생했습니다.", null));
    }
}