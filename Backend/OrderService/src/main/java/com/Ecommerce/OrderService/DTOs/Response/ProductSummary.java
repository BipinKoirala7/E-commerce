package com.Ecommerce.OrderService.DTOs.Response;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Embeddable
public class ProductSummary {

  private UUID id;
  private String name;
  private BigDecimal price;
  private String imageUrl;
  private String brand;
}
