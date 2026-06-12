package com.Ecommerce.OrderService.DTOs.Request;

import com.Ecommerce.OrderService.Model.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderUpdateDTO {

  @NotNull
  private OrderStatus orderStatus;
}
