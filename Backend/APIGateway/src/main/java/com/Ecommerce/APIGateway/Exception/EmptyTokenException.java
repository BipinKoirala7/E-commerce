package com.Ecommerce.APIGateway.Exception;

public class EmptyTokenException extends RuntimeException {
  public EmptyTokenException(String message) {
    super(message);
  }
}
