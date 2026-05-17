package com.Ecommerce.OrderService.DTOs.Response;

import com.Ecommerce.OrderService.Model.Order;
import com.Ecommerce.OrderService.Model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class OrderListResponseDTO {

  @NotNull
  private UUID id;

  @NotNull
  private String orderNumber;

  @NotNull
  private Integer noOfItems;

  @NotNull
  private BigDecimal totalPrice;

  @NotNull
  private OrderStatus orderStatus;

  private LocalDateTime createdAt;

  @NotNull
  private String billingAddress;
}
