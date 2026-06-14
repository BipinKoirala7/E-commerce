package com.Ecommerce.OrderService.Exception;

public class PaymentNotFound extends RuntimeException {
  public PaymentNotFound(String message) {
    super(message);
  }
}
