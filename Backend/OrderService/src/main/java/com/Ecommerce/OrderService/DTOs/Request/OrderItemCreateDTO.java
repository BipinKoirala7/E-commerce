package com.Ecommerce.OrderService.DTOs.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderItemCreateDTO {

  @NotNull
  private UUID productId;

  @NotNull
  private Integer quantity;
}
