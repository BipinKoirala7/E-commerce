package com.Ecommerce.UserService.Exception;

import com.Ecommerce.UserService.DTOs.Response.RestApiResponse;
import feign.FeignException;
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
/**
 * Exception Handler for whole service. (except Filters).
 *
 * @see UserAlreadyExistsException
 * @see IncorrectEmailOrPasswordException
 * @see InValidTokenException
 * @see EmptyTokenException
 * @see AuthenticationException
 * @see UserNotFoundException
 * @see UnVerifiedSourceException
 * **/
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<RestApiResponse<Void>> handleUserAlreadyExistsException(@NonNull UserAlreadyExistsException e) {
    log.warn("User Already Exists Exception Occurred");
    log.warn("Exception message: {}", e.getMessage());
    String message = "User with given email  already exists";

    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(RestApiResponse.error(HttpStatus.CONFLICT.value(), message));
  }

  @ExceptionHandler(IncorrectEmailOrPasswordException.class)
  public ResponseEntity<RestApiResponse<Void>> handleIncorrectEmailOrPasswordException(@NonNull IncorrectEmailOrPasswordException e) {
    log.warn("Incorrect Email Or Password Exception Exception Occurred");
    log.warn("Exception message: {}", e.getMessage());
    String message = "Email or Password is incorrect";

    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(RestApiResponse.error(HttpStatus.CONFLICT.value(), message));
  }

  @ExceptionHandler(InValidTokenException.class)
  public ResponseEntity<RestApiResponse<Void>> handleInValidTokenTypeException(@NonNull InValidTokenException e) {
    log.warn("InValid Token Type Exception Occurred");
    log.warn("Exception message: {}", e.getMessage());
    String message = "Authentication Failed. Please log in again!";

    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(RestApiResponse.error(HttpStatus.UNAUTHORIZED.value(), message));
  }

  @ExceptionHandler(EmptyTokenException.class)
  public ResponseEntity<RestApiResponse<Void>> handleEmptyTokenException(@NonNull EmptyTokenException e) {
    log.warn("Empty Token Exception Occurred");
    log.warn("Exception message: {}", e.getMessage());
    String message = "Authentication Failed. Please log in again!";

    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(RestApiResponse.error(HttpStatus.UNAUTHORIZED.value(), message));
  }

  @ExceptionHandler(UserAuthenticationFailedException.class)
  public ResponseEntity<RestApiResponse<Void>> handleUserAuthenticationFailedException(@NonNull UserAuthenticationFailedException e) {
    log.warn("User Authentication Failed Exception Occurred");
    log.warn("Exception message: {}", e.getMessage());
    String message = "Authentication Failed. Please Try Again!";

    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(RestApiResponse.error(HttpStatus.UNAUTHORIZED.value(), message));
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<RestApiResponse<Void>> handleUserNotFoundException(@NonNull UserNotFoundException e) {
    log.warn("UserNotFound Exception Occurred");
    log.warn("Exception message: {}", e.getMessage());
    String message = "User doesn't exists";

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

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<RestApiResponse<Void>> handleConstraintViolationException(@NonNull ConstraintViolationException e) {
    log.error("Constraint Violation Exception Occurred");
    log.error("Exception Message: {}", e.getMessage());

    String validationErrors = e.getConstraintViolations()
        .stream()
        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
        .collect(Collectors.joining(", "));

    log.error("Validation errors: {}", validationErrors);

    String message = "Something went wrong";

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(RestApiResponse.error(HttpStatus.BAD_REQUEST.value(), message));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<RestApiResponse<Void>> handleDataIntegrityViolationException(@NonNull DataIntegrityViolationException e) {
    log.error("Data Integrity Violation Exception Occurred");
    log.error("Exception Message : {}", e.getMessage());
    String message = "Something went wrong Please try again";

    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(RestApiResponse.error(HttpStatus.CONFLICT.value(), message));
  }

  @ExceptionHandler(TransactionSystemException.class)
  public ResponseEntity<RestApiResponse<Void>> handleTransactionSystemException(
      @NonNull TransactionSystemException e) {
    log.error("Transaction System Exception Occurred");
    log.error("Exception Message: {}", e.getMessage());
    log.error("Transaction could not be completed");
    String message = "Something went wrong. Please try again.";

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(RestApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
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

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<RestApiResponse<Void>> handleIllegalArgumentException(@NonNull IllegalArgumentException e) {
    log.warn("Illegal Argument Exception Occurred");
    log.warn("Exception Message: {}", e.getMessage());
    String message = "Please provide the required information";

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(RestApiResponse.error(HttpStatus.BAD_REQUEST.value(), message));
  }

  // It handles General Exception, which is not handled by other handlers
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
