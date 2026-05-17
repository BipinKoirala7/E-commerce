package com.Ecommerce.OrderService.DTOs.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class OrderItemResponseDTO {
  private UUID id;
  private ProductSummary productSummary;
  private Integer quantity;
}
