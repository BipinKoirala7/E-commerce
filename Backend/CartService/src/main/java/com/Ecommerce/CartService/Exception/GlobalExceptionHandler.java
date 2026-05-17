package com.Ecommerce.CartService.Exception;

import com.Ecommerce.CartService.DTOs.Response.RestApiResponse;
import feign.FeignException;
import feign.RetryableException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(ProductAlreadyInWishlistException.class)
  public ResponseEntity<RestApiResponse<Void>> handleProductAlreadyInWishlistException(@NonNull ProductAlreadyInWishlistException e) {
    log.warn("Product Already In Wishlist Exception Occurred");
    log.warn("Exception Message: {}", e.getMessage());
    String message = "Product Already In Wishlist";

    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(RestApiResponse.error(HttpStatus.CONFLICT.value(), message));
  }

  @ExceptionHandler(CartItemNotFound.class)
  public ResponseEntity<RestApiResponse<Void>> handleCartItemNotFoundException(@NonNull CartItemNotFound e) {
    log.warn("Cart Item Not Found Exception Occurred");
    log.warn("Exception Message: {}", e.getMessage());
    String message = "Cart Item Not Found";

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(RestApiResponse.error(HttpStatus.NOT_FOUND.value(), message));
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<RestApiResponse<Void>> handleExpiredJwt(@NonNull ExpiredJwtException e) {
    log.info("Expired Jwt Exception Occurred");
    log.warn("Exception message : {}", e.getMessage());
    String message = "Authentication Failed. Please log in again!";

    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(RestApiResponse.error(HttpStatus.UNAUTHORIZED.value(), message));
  }

  @ExceptionHandler(SignatureException.class)
  public ResponseEntity<RestApiResponse<Void>> handleInvalidSignature(@NonNull SignatureException e) {
    log.info("Signature Exception Occurred");
    log.warn("Exception message : {}", e.getMessage());
    String message = "Authentication Failed. Please log in again!";

    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(RestApiResponse.error(HttpStatus.UNAUTHORIZED.value(), message));
  }

  @ExceptionHandler(MalformedJwtException.class)
  public ResponseEntity<RestApiResponse<Void>> handleMalformedJwt(@NonNull MalformedJwtException e) {
    log.info("Malformed Jwt Exception Occurred");
    log.warn("Exception message : {}", e.getMessage());
    String message = "Authentication Failed. Please log in again!";

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(RestApiResponse.error(HttpStatus.BAD_REQUEST.value(), message));
  }

  @ExceptionHandler(UnsupportedJwtException.class)
  public ResponseEntity<RestApiResponse<Void>> handleUnsupportedJwt(@NonNull UnsupportedJwtException e) {
    log.info("Unsupported Jwt Exception Occurred");
    log.warn("Exception message : {}", e.getMessage());
    String message = "Authentication Failed. Please log in again!";

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(RestApiResponse.error(HttpStatus.BAD_REQUEST.value(), message));
  }

  @ExceptionHandler(JwtException.class)
  public ResponseEntity<RestApiResponse<Void>> handleJwtException(@NotNull @NonNull JwtException e) {
    log.warn("JWT Exception Occurred");
    log.error("Exception Message: {}", e.getMessage());
    String message = "Authentication Failed. Please log in again!";

    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(RestApiResponse.error(HttpStatus.UNAUTHORIZED.value(), message));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<RestApiResponse<Void>> handleAuthenticationException(@NotNull @NonNull AuthenticationException e) {
    log.error("Authentication Exception Occurred");
    log.error("Exception Message : {}", e.getMessage());
    String message = "Authentication Failed. Please log in again!";

    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(RestApiResponse.error(HttpStatus.UNAUTHORIZED.value(), message));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<RestApiResponse<Void>> handleAccessDenied(@NonNull AccessDeniedException e) {
    log.error("Access Denied Exception Occurred");
    log.error("Exception Message : {}", e.getMessage());
    String message = "Access Denied";

    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(RestApiResponse.error(HttpStatus.FORBIDDEN.value(), message));
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


  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<RestApiResponse<Void>> handleDataAccessException(@NonNull DataAccessException e) {
    log.error("Data Access Exception Occurred");
    log.error("Database query execution failed");
    log.error("Exception Message : {}", e.getMessage());
    String message = "Service is temporarily unavailable";

    return ResponseEntity
        .status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(RestApiResponse.error(HttpStatus.SERVICE_UNAVAILABLE.value(), message));
  }

  @ExceptionHandler(ResourceAccessException.class)
  public ResponseEntity<RestApiResponse<Void>> handleServiceDown(@NonNull ResourceAccessException e) {
    log.error("Resource Access Exception Occurred");
    log.error("System Resource unavailable");
    log.error("Exception Message : {}", e.getMessage());
    String message = "Service is temporarily unavailable";

    return ResponseEntity
        .status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(RestApiResponse.error(HttpStatus.SERVICE_UNAVAILABLE.value(), message));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<RestApiResponse<Void>> handleConstraintViolationException(@NonNull ConstraintViolationException e) {
    log.error("Constraint Violation Exception Occurred");
    log.error("Exception Message: {}", e.getMessage());

    String validationErrors = e.getConstraintViolations()
        .stream()
        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
        .collect(Collectors.joining(", "));

    log.error("Validation errors: {}", validationErrors);

    String message = "Please provide only valid info";

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
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
  public ResponseEntity<RestApiResponse<Void>> handleTransactionSystemException(
      @NonNull TransactionSystemException e) {
    log.error("Transaction System Exception Occurred");
    log.error("Exception Message: {}", e.getMessage());

    String message = "Transaction could not be completed";

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(RestApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<RestApiResponse<Void>> handleIllegalArgumentException(@NonNull IllegalArgumentException e) {
    log.warn("Illegal Argument Exception Occurred");
    log.warn("Exception Message: {}", e.getMessage());
    String message = "Please provide only valid info";

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(RestApiResponse.error(HttpStatus.BAD_REQUEST.value(), message));
  }

  // It handles General Exception, which is not handled by other handlers
  @ExceptionHandler(Exception.class)
  public ResponseEntity<RestApiResponse<?>> handleGeneralException(@NonNull Exception e) {
    log.warn("General Exception Occurred");
    log.warn("Exception : {}", e.getClass().getName());
    log.warn("Exception Message: {}", e.getMessage());
    String message = "Something went wrong, Please try again!";

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(RestApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
  }

  //  Helper Methods
  private @NonNull String extractErrorMessage(@NonNull FeignException e, HttpStatus status) {
    String contentUTF8 = e.contentUTF8();

    // Try to extract the response body from a downstream service
    if (contentUTF8 != null && !contentUTF8.isBlank()) {
      // Truncate if too long
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
      case NOT_FOUND -> e.getMessage() != null ? e.getMessage() : "Not Found";
      case INTERNAL_SERVER_ERROR -> e.getMessage() != null ? e.getMessage() : "Something went wrong. Please try again";
      case SERVICE_UNAVAILABLE ->
          e.getMessage() != null ? e.getMessage() : "Service is temporarily unavailable. Please try again";
      default -> "Service request failed with status " + status.value();
    };
  }
}
