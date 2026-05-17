package com.Ecommerce.UserService.Exception;

public class EmptyTokenException extends RuntimeException {
  public EmptyTokenException(String message) {
    super(message);
  }
}
