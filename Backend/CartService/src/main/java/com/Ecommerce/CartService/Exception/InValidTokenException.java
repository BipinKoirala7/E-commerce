package com.Ecommerce.CartService.Exception;

public class InValidTokenException extends RuntimeException {
  public InValidTokenException(String message) {
    super(message);
  }
}
