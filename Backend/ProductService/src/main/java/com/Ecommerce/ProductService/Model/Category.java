package com.Ecommerce.ProductService.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.jspecify.annotations.NonNull;

public enum Category {
  MEN,
  WOMEN,
  ALL;

  @JsonCreator
  public static Category fromString(@NonNull String value) {
    return Category.valueOf(value.toUpperCase().trim());
  }
}
