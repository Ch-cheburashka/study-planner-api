package com.masha.studyplannerapi.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiError handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String,String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));
        Map<String, Object> errors = new HashMap<>();

        errors.put("error", "validation failed");
        errors.put("fields", fieldErrors);
        return new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request.getRequestURI(),
                errors
        );
    }
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TaskNotFoundException.class)
    public ApiError handleTaskNotFound(TaskNotFoundException ex, HttpServletRequest request) {
        Map<String,Object> errors = new HashMap<>();
        errors.put("error", "task not found");
        return new ApiError(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI(),
                errors
        );
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiError handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        Map<String,Object> errors = new HashMap<>();
        errors.put("error", "invalid request body");
        return new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                ex.getMostSpecificCause().getMessage(),
                request.getRequestURI(),
                errors
        );
    }
}
