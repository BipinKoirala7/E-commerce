package com.Ecommerce.OrderService.DTOs.Response;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Embeddable
@NoArgsConstructor
public class ProductSummary {

  private UUID id;
  private String name;
  private BigDecimal price;
  private String imageUrl;
  private String brand;
}
