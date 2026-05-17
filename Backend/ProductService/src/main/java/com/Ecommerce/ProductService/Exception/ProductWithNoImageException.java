package com.Ecommerce.ProductService.Exception;

public class ProductWithNoImageException extends RuntimeException {
  public ProductWithNoImageException(String message) {
    super(message);
  }
}
