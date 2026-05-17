package com.Ecommerce.ProductService.DTOs.Request;

import com.Ecommerce.ProductService.Model.Category;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreateDTO {

  @NotNull
  private String name;

  @NotNull
  private String description;

  @NotNull
  private BigDecimal price;

  @NotNull
  private Integer stockQuantity;

  @NotNull
  private Category category;

  @NotNull
  private String imageUrl;

  @NotNull
  private String brand;
}
