package org.example.demo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // ✅ Handle 404: Resource not found
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(
      ResourceNotFoundException ex, HttpServletRequest request) {

    ErrorResponse error = ErrorResponse.of(
        404, "Not Found", ex.getMessage(), request.getRequestURI());
    return ResponseEntity.status(404).body(error);
  }

  // ✅ Handle 400: Bad request / validation
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleBadRequest(
      IllegalArgumentException ex, HttpServletRequest request) {

    ErrorResponse error = ErrorResponse.of(
        400, "Bad Request", ex.getMessage(), request.getRequestURI());
    return ResponseEntity.badRequest().body(error);
  }

  // ✅ Handle 500: Generic server error (for dev)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(
      Exception ex, HttpServletRequest request) {

    // 🚨 In production: log ex.getMessage() instead of exposing
    ErrorResponse error = ErrorResponse.of(
        500, "Internal Server Error", ex.getMessage(), request.getRequestURI());
    return ResponseEntity.status(500).body(error);
  }
}
