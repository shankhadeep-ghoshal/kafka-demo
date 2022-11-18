package com.shankhadeepghoshal.learnkafka.taxiproducer.exception;

import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ValidationErrorResponse onConstraintValidationException(ConstraintViolationException e) {
    final var error = new ValidationErrorResponse(new ArrayList<>());

    error
        .violations()
        .addAll(
            e.getConstraintViolations().stream()
                .map(
                    violation ->
                        new Violation(
                            violation.getPropertyPath().toString(), violation.getMessage()))
                .toList());
    return error;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    ValidationErrorResponse error = new ValidationErrorResponse(new ArrayList<>());
    error
        .violations()
        .addAll(
            e.getBindingResult().getFieldErrors().stream()
                .map(
                    fieldError ->
                        new Violation(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList());

    return error;
  }

  public record Violation(String fieldName, String message) {}

  public record ValidationErrorResponse(List<Violation> violations) {}
}
