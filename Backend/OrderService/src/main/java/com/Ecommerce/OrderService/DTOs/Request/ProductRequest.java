package com.Ecommerce.OrderService.DTOs.Request;

import com.Ecommerce.OrderService.DTOs.Response.OrderItemResponseDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductRequest {
  private String orderNumber;
  private List<OrderItemResponseDTO> orderItems;
  private String currency;
}
