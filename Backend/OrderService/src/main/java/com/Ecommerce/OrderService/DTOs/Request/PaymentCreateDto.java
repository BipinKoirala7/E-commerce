package com.Ecommerce.OrderService.DTOs.Request;

import com.Ecommerce.OrderService.Model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentCreateDto {

  @NotNull
  private PaymentMethod paymentMethod;
}
