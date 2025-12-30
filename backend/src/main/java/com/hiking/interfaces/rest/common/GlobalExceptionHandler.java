package com.hiking.interfaces.rest.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        return ApiResponse.error(400, message);
    }

    /**
     * 处理缺少请求头异常
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleMissingHeaderException(MissingRequestHeaderException e) {
        log.warn("缺少请求头: {}", e.getHeaderName());
        return ApiResponse.error(400, "缺少必要的请求头: " + e.getHeaderName());
    }

    /**
     * 处理业务参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("业务参数异常: {}", e.getMessage());
        return ApiResponse.error(400, e.getMessage());
    }

    /**
     * 处理业务状态异常
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void> handleIllegalStateException(IllegalStateException e) {
        log.warn("业务状态异常: {}", e.getMessage());
        return ApiResponse.error(403, e.getMessage());
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return ApiResponse.error(500, "系统内部错误");
    }
}
