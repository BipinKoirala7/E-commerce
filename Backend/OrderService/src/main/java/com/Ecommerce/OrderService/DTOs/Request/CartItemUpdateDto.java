package com.Ecommerce.OrderService.DTOs.Request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemUpdateDto {

  @NotNull
  private Integer quantity;
}
