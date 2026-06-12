package com.Ecommerce.OrderService.Exception;

public class InValidOrderUpdate extends RuntimeException {
  public InValidOrderUpdate(String message) {
    super(message);
  }
}
