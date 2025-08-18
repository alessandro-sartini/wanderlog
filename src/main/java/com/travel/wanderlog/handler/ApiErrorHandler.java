package com.travel.wanderlog.handler;

import java.util.Map;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiErrorHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus()
  Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
    var errors = ex.getBindingResult().getFieldErrors().stream()
      .map(fe -> Map.of(
        "field", fe.getField(),
        "message", fe.getDefaultMessage()
      )).toList();
    return Map.of(
      "status", 400,
      "error", "Bad Request",
      "errors", errors
    );
  }
}
