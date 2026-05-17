package com.Ecommerce.OrderService.DTOs.Response;

import com.Ecommerce.OrderService.Model.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderDetailsResponseDTO {

  @NotNull
  private UUID id;

  @NotNull
  private String orderNumber;

  @NotNull
  private String billingAddress;

  @NotNull
  private String shippingAddress;

  @NotNull
  private String email;

  @NotNull
  private String phone;

  @NotEmpty
  private List<OrderItemResponseDTO> orderItems;

  @NotNull
  private BigDecimal totalPrice;

  @NotNull
  private OrderStatus orderStatus;

  private LocalDateTime createdAt;

  @NotNull
  private LocalDateTime updatedAt;
}
