package com.Ecommerce.APIGateway.Exception;

import com.Ecommerce.APIGateway.DTOs.RestApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<RestApiResponse<Void>> handleNoResourceException(@NonNull NoResourceFoundException e) {
    log.debug("No Resource Found Exception Occurred");
    log.debug("Exception message: {}", e.getMessage());
    String message = "No Resource Found";

    return ResponseEntity.
        status(HttpStatus.BAD_REQUEST)
        .body(RestApiResponse.error(HttpStatus.BAD_REQUEST.value(), message));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<RestApiResponse<Void>> handleGeneralException(@NonNull Exception e) {
    log.warn("General Exception Occurred");
    log.warn("Exception : {}", e.getClass().getName());
    log.warn("Exception Message: {}", e.getMessage());
    String message = "An unexpected error occurred. Please try again.";

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(RestApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
  }
}
