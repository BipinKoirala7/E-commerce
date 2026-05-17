package com.Ecommerce.OrderService.DTOs.Request;

import com.Ecommerce.OrderService.DTOs.Response.OrderItemResponseDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ProductRequest {
  private UUID orderId;
  private List<OrderItemResponseDTO> orderItems;
  private String currency;
}
