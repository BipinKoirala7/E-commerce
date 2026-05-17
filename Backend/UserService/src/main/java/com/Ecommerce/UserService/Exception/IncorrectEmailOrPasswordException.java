package com.Ecommerce.UserService.Exception;

public class IncorrectEmailOrPasswordException extends RuntimeException {
  public IncorrectEmailOrPasswordException(String message) {
    super(message);
  }
}
