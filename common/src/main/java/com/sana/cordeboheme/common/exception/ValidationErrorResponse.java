package com.sana.cordeboheme.common.exception;

import java.util.Map;

public record ValidationErrorResponse(
    java.time.LocalDateTime now,
    int status,
    String error,
    String message,
    String path,
    Map<String, String> validationErrors) {}
