package com.Ecommerce.OrderService.Exception;

public class ZeroItemQuantityInOrderException extends RuntimeException {
  public ZeroItemQuantityInOrderException(String message) {
    super(message);
  }
}
