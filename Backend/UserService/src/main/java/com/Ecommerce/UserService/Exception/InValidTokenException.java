package com.Ecommerce.UserService.Exception;

public class InValidTokenException extends RuntimeException {
  public InValidTokenException(String message) {
    super(message);
  }
}
