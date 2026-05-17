package com.Ecommerce.APIGateway.Exception;

public class InValidTokenException extends RuntimeException {
  public InValidTokenException(String message) {
    super(message);
  }
}
