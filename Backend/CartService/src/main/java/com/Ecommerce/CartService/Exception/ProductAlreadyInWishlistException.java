package com.Ecommerce.CartService.Exception;

public class ProductAlreadyInWishlistException extends RuntimeException {
  public ProductAlreadyInWishlistException(String message) {
    super(message);
  }
}
