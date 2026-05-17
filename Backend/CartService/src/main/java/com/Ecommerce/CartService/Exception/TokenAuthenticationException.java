package com.Ecommerce.CartService.Exception;

public class TokenAuthenticationException extends RuntimeException {
  public TokenAuthenticationException(String message) {
    super(message);
  }
}
