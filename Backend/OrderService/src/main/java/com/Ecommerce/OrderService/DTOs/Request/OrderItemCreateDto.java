package com.Ecommerce.OrderService.DTOs.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderItemCreateDto {

  @NotNull
  private UUID productId;

  @NotNull
  private Integer quantity;
}
