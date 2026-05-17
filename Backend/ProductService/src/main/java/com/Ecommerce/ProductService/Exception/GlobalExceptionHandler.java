package com.Ecommerce.ProductService.Exception;

import com.Ecommerce.ProductService.DTOs.Response.RestApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ProductWithNoImageException.class)
  public ResponseEntity<RestApiResponse<?>> handleProductNotFoundException(@NonNull ProductWithNoImageException e) {
    log.warn("Product with no images Exception Occurred");
    log.warn("Exception Message: {}", e.getMessage());
    String message = "Product cannot be created without images. Please add at least one image to the product!";

    return ResponseEntity
        .status(HttpStatus.NOT_ACCEPTABLE)
        .body(RestApiResponse.error(HttpStatus.NOT_ACCEPTABLE.value(), message));
  }

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<RestApiResponse<?>> handleProductNotFoundException(@NonNull ProductNotFoundException e) {
    log.warn("Product Not Found Exception Occurred");
    log.warn("Exception Message: {}", e.getMessage());
    String message = "Product Not Found";

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(RestApiResponse.error(HttpStatus.NOT_FOUND.value(), message));
  }

  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<RestApiResponse<Void>> handleDataAccessException(@NonNull DataAccessException e) {
    log.error("Data Access Exception Occurred");
    log.error("Database query execution failed. Database service is temporarily unavailable");
    log.error("Exception Message : {}", e.getMessage());
    String message = "Something went wrong. Please try again!";

    return ResponseEntity
        .status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(RestApiResponse.error(HttpStatus.SERVICE_UNAVAILABLE.value(), message));
  }

  @ExceptionHandler(ResourceAccessException.class)
  public ResponseEntity<RestApiResponse<Void>> handleServiceDown(@NonNull ResourceAccessException e) {
    log.error("Resource Access Exception Occurred");
    log.error("System Resource unavailable");
    log.error("Exception Message : {}", e.getMessage());
    String message = "Something went wrong. Please try again!";

    return ResponseEntity
        .status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(RestApiResponse.error(HttpStatus.SERVICE_UNAVAILABLE.value(), message));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<RestApiResponse<?>> handleIllegalArgumentException(@NonNull IllegalArgumentException e) {
    log.warn("Illegal Argument Error Occurred");
    log.warn("Exception Message: {}", e.getMessage());
    String message = "Something went wrong. Please Try again!";

    return ResponseEntity
        .status(HttpStatus.NOT_ACCEPTABLE)
        .body(RestApiResponse.error(HttpStatus.NOT_ACCEPTABLE.value(), message));
  }

  // It handles General Exception, which is not handled by other handlers
  @ExceptionHandler(Exception.class)
  public ResponseEntity<RestApiResponse<?>> handleGeneralException(@NonNull Exception e) {
    log.warn("General Exception Occurred");
    log.warn("Exception : {}", e.getClass().getName());
    log.warn("Exception Message: {}", e.getMessage());
    String message = "Something went wrong. Please Try again!";

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(RestApiResponse.error(HttpStatus.BAD_REQUEST.value(), message));
  }
}
