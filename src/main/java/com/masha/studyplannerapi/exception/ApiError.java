package com.masha.studyplannerapi.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiError(
        LocalDateTime timestamp,
        HttpStatus status,
        String message,
        String path,
        Map<String, Object> fieldErrors
) {
}
