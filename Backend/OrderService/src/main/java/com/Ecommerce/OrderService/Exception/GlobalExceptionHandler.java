package com.Ecommerce.OrderService.Exception;

import com.Ecommerce.OrderService.DTOs.Response.RestApiResponse;
import feign.FeignException;
import feign.RetryableException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(ZeroItemQuantityInOrderException.class)
  public ResponseEntity<RestApiResponse<Void>> handleTokenAuthenticationException(@NonNull ZeroItemQuantityInOrderException e) {
    log.warn("Zero Item Quantity In Order Excpetion Occurred");
    log.warn("Exception Message: {}", e.getMessage());

    return ResponseEntity
        .status(HttpStatus.NOT_ACCEPTABLE)
        .body(RestApiResponse.error(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage()));
  }

  @ExceptionHandler(TokenAuthenticationException.class)
  public ResponseEntity<RestApiResponse<Void>> handleTokenAuthenticationException(@NonNull TokenAuthenticationException e) {
    log.warn("Empty Cart Exception Occurred");
    log.warn("Exception Message: {}", e.getMessage());

    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(RestApiResponse.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
  }

  @ExceptionHandler(EmptyProductsOrderCreationException.class)
  public ResponseEntity<RestApiResponse<Void>> handleEmptyCartOrderCreationException(@NonNull EmptyProductsOrderCreationException e) {
    log.warn("Empty Cart Exception Occurred");
    log.warn("Exception Message: {}", e.getMessage());

    return ResponseEntity
        .status(HttpStatus.NOT_ACCEPTABLE)
        .body(RestApiResponse.error(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage()));
  }

  @ExceptionHandler(OrderNotFound.class)
  public ResponseEntity<RestApiResponse<Void>> handleOrderNotFoundException(@NonNull OrderNotFound e) {
    log.warn("Order Not Found Exception Occurred");
    log.warn("Exception Message: {}", e.getMessage());

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(RestApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
  }

  @ExceptionHandler(FeignException.FeignClientException.class)
  public ResponseEntity<RestApiResponse<Void>> handleFeignClientError(FeignException.@NonNull FeignClientException e) {
    log.error("Feign Client Exception Occurred");
    log.error("Exception Message : {}", e.getMessage());

    HttpStatus status = HttpStatus.valueOf(e.status());
    String message = extractErrorMessage(e, status);

    return ResponseEntity
        .status(status)
        .body(RestApiResponse.error(e.status(), message));
  }

  @ExceptionHandler(FeignException.FeignServerException.class)
  public ResponseEntity<RestApiResponse<Void>> handleFeignServerError(FeignException.@NonNull FeignServerException e) {
    log.error("Feign Server Exception Occurred");
    log.error("Exception Message : {}", e.getMessage());

    HttpStatus status = HttpStatus.valueOf(e.status());
    String message = extractErrorMessage(e, status);

    return ResponseEntity
        .status(status)
        .body(RestApiResponse.error(e.status(), message));
  }

  @ExceptionHandler(FeignException.class)
  public ResponseEntity<RestApiResponse<Void>> handleFeignException(@NonNull FeignException e) {
    log.error("Feign Exception Occurred");
    log.error("Exception Message : {}", e.getMessage());

    int statusCode = e.status() != -1 ? e.status() : HttpStatus.INTERNAL_SERVER_ERROR.value();
    HttpStatus status = HttpStatus.valueOf(statusCode);
    String message = extractErrorMessage(e, status);

    return ResponseEntity
        .status(status)
        .body(RestApiResponse.error(statusCode, message));
  }

  @ExceptionHandler(RetryableException.class)
  public ResponseEntity<RestApiResponse<Void>> handleRetryableException(@NonNull RetryableException e) {
    log.error("Retryable Exception Occurred");
    log.error("Feign Retryable Exception (timeout/connection)");
    log.error("Exception Message : {}", e.getMessage());
    String message = "Service is temporarily unavailable. Please try again.";

    return ResponseEntity
        .status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(RestApiResponse.error(HttpStatus.SERVICE_UNAVAILABLE.value(), message));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<RestApiResponse<Void>> handleConstraintViolationException(@NonNull ConstraintViolationException e) {
    log.error("Constraint Violation Exception Occurred");
    log.error("Exception Message: {}", e.getMessage());

    // Extract specific validation errors for better user feedback
    String validationErrors = e.getConstraintViolations()
        .stream()
        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
        .collect(Collectors.joining(", "));

    log.error("Validation errors: {}", validationErrors);

    String message = "Please provide only valid info";

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST) // 400 instead of 503
        .body(RestApiResponse.error(HttpStatus.BAD_REQUEST.value(), message));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<RestApiResponse<Void>> handleDataIntegrityViolationException(@NonNull DataIntegrityViolationException e) {
    log.error("Data Integrity Violation Exception Occurred");
    log.error("Exception Message : {}", e.getMessage());
    String message = "Database constraint violated";

    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(RestApiResponse.error(HttpStatus.CONFLICT.value(), message));
  }

  @ExceptionHandler(TransactionSystemException.class)
  public ResponseEntity<RestApiResponse<Void>> handleTransactionSystemException(@NonNull TransactionSystemException e) {
    log.error("Transaction System Exception Occurred");
    log.error("Exception Message: {}", e.getMessage());

    // Unwrap the root cause
    Throwable cause = e.getRootCause();

    if (cause instanceof ConstraintViolationException constraintViolationException) {
      String validationErrors = constraintViolationException.getConstraintViolations()
          .stream()
          .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
          .collect(Collectors.joining(", "));

      log.error("Validation errors : {}", validationErrors);

      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(RestApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Please provide only valid info"));
    }

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(RestApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Transaction could not be completed"));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<RestApiResponse<?>> handleIllegalArgumentException(@NonNull IllegalArgumentException e) {
    log.warn("Illegal Argument Exception Occurred");
    log.warn("Exception Message: {}", e.getMessage());

    return ResponseEntity
        .status(HttpStatus.NOT_ACCEPTABLE)
        .body(RestApiResponse.error(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage()));
  }

  // It handles General Exception, which is not handled by other handlers
  @ExceptionHandler(Exception.class)
  public ResponseEntity<RestApiResponse<?>> handleGeneralException(@NonNull Exception e) {
    log.warn("General Exception Occurred");
    log.warn("Exception : {}", e.getClass().getName());
    log.warn("Exception Message: {}", e.getMessage());

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(RestApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR
            .value(), e.getMessage()));
  }

  //  Helper function

  private @NonNull String extractErrorMessage(@NonNull FeignException e, HttpStatus status) {
    String contentUTF8 = e.contentUTF8();

    if (contentUTF8 != null && !contentUTF8.isBlank()) {
      if (contentUTF8.length() > 200) {
        return contentUTF8.substring(0, 200) + "...";
      }
      return contentUTF8;
    }

    // Fallback to status-specific messages
    return switch (status) {
      case BAD_REQUEST -> e.getMessage() != null ? e.getMessage() : "Bad request";
      case UNAUTHORIZED -> e.getMessage() != null ? e.getMessage() : "Authentication failed. Please log in again!";
      case FORBIDDEN -> e.getMessage() != null ? e.getMessage() : "Access denied";
      case NOT_FOUND -> "Not Found";
      case INTERNAL_SERVER_ERROR -> e.getMessage() != null ? e.getMessage() : "Something went wrong. Please try again";
      case SERVICE_UNAVAILABLE ->
          e.getMessage() != null ? e.getMessage() : "Service is temporarily unavailable. Please try again";
      default -> "Service request failed with status " + status.value();
    };
  }
}
