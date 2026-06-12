package com.Ecommerce.OrderService.Exception;

public class CartItemNotFound extends RuntimeException {
  public CartItemNotFound(String message) {
    super(message);
  }
}
