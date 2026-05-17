package com.Ecommerce.CartService.Exception;

public class EmptyTokenException extends RuntimeException {
  public EmptyTokenException(String message) {
    super(message);
  }
}
