package com.Ecommerce.OrderService.Exception;

public class OrderNotFound extends RuntimeException {
  public OrderNotFound(String message) {
    super(message);
  }
}
