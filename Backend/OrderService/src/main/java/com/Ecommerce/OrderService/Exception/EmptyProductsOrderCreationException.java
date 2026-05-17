package com.Ecommerce.OrderService.Exception;

public class EmptyProductsOrderCreationException extends RuntimeException {
  public EmptyProductsOrderCreationException(String message) {
    super(message);
  }
}
