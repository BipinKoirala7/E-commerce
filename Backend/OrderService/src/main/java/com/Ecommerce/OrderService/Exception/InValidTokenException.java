package com.Ecommerce.OrderService.Exception;

public class InValidTokenException extends RuntimeException {
  public InValidTokenException(String message) {
    super(message);
  }
}
