package com.Ecommerce.OrderService.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @NotNull
  @Column(nullable = false)
  private UUID orderId;

  @NotNull
  @Column(nullable = false, unique = true)
  private String paymentNumber;

  @NotNull
  @Column(nullable = false, unique = true)
  private String stripeSessionId;

  @Column(unique = true)
  private String stripePaymentIntentId;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentStatus paymentStatus;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentMethod paymentMethod;

  @NotNull
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal totalPrice;

  @CreationTimestamp
  @Column(nullable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime updatedAt;
}
