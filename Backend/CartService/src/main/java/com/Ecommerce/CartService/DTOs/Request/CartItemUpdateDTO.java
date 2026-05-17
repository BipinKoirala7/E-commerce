package com.Ecommerce.CartService.DTOs.Request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemUpdateDTO {

  @NotNull
  private Integer quantity;
}
