package com.Ecommerce.OrderService.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @NotNull
  @Column(nullable = false)
  private UUID productId;

  @NotNull
  @Column(nullable = false)
  @Min(1)
  private Integer quantity;

  // Maybe we can add price here.
}
