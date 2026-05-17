package com.Ecommerce.CartService.Exception;

public class CartItemNotFound extends RuntimeException {
  public CartItemNotFound(String message) {
    super(message);
  }
}
