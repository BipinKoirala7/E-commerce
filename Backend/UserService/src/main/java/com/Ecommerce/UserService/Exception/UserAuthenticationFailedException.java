package com.Ecommerce.UserService.Exception;

public class UserAuthenticationFailedException extends RuntimeException {
  public UserAuthenticationFailedException(String message) {
    super(message);
  }
}
