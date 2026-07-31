package com.sana.cordeboheme.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ResourceNotFoundException.class)
  ResponseEntity<ErrorResponse> handleResourceNotFound(
      ResourceNotFoundException ex, HttpServletRequest request) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(ResourceAlreadyExistsException.class)
  ResponseEntity<ErrorResponse> handleResourceAlreadyExist(
      ResourceAlreadyExistsException ex, HttpServletRequest request) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ValidationErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult()
        .getFieldErrors()
        .forEach(
            error -> errors.put(error.getField(), error.getDefaultMessage()));

    ValidationErrorResponse errorResponse =
        new ValidationErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "Validation failed",
            request.getRequestURI(),
            errors);

    return ResponseEntity.badRequest().body(errorResponse);
  }
}
