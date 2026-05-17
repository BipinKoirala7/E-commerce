package com.Ecommerce.ProductService.DTOs.Response;

import com.Ecommerce.ProductService.Model.Category;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductSummary {

  private UUID id;
  private String name;
  private BigDecimal price;
  private Category category;
  private String imageUrl;
  private String brand;
}
