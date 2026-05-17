package com.Ecommerce.ProductService.DTOs.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateDTO {

  @NotNull
  private String name;

  @NotNull
  private String description;

  @NotNull
  private BigDecimal price;

  @NotNull
  private Integer stockQuantity;

  @NotNull
  private String imageUrl;
}
