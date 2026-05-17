package com.Ecommerce.OrderService.Exception;

public class EmptyTokenException extends RuntimeException {
  public EmptyTokenException(String message) {
    super(message);
  }
}
