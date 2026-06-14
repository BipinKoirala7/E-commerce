package com.Ecommerce.OrderService.DTOs.Response;

import com.Ecommerce.OrderService.Model.PaymentMethod;
import com.Ecommerce.OrderService.Model.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentResponseDto {
  private UUID id;
  private UUID orderId;
  private String paymentNumber;
  private PaymentStatus paymentStatus;
  private PaymentMethod paymentMethod;
  private BigDecimal totalAmount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
