package com.Ecommerce.OrderService.Exception;

public class TokenAuthenticationException extends RuntimeException {
  public TokenAuthenticationException(String message) {
    super(message);
  }
}
