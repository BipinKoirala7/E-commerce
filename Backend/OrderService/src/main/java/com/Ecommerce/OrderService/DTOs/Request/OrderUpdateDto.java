package com.Ecommerce.OrderService.DTOs.Request;

import com.Ecommerce.OrderService.Model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderUpdateDto {

  @NotNull
  private OrderStatus orderStatus;
}
