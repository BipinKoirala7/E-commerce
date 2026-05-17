package com.Ecommerce.UserService.DTOs.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RestApiResponse<T> {
  private Integer statusCode;
  private Boolean success;
  private T data;
  private String message;
  private LocalDateTime timestamp;

  public static <T> RestApiResponse<T> success(Integer statusCode, T data, String message) {
    return RestApiResponse.<T>builder()
        .statusCode(statusCode)
        .success(true)
        .data(data)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }

  public static RestApiResponse<Void> success(Integer statusCode, String message) {
    return RestApiResponse.<Void>builder()
        .statusCode(statusCode)
        .success(true)
        .data(null)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }

  public static RestApiResponse<Void> error(Integer statusCode, String message) {
    return RestApiResponse.<Void>builder()
        .statusCode(statusCode)
        .success(false)
        .data(null)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }
}
