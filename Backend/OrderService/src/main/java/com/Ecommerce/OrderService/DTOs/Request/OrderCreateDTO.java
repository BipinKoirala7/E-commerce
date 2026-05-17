package com.Ecommerce.OrderService.DTOs.Request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateDTO {

  @NotNull
  private String billingAddress;

  @NotNull
  private String shippingAddress;

  @NotNull
  private String phone;

  @NotEmpty
  private List<OrderItemCreateDTO> orderItems;

}
