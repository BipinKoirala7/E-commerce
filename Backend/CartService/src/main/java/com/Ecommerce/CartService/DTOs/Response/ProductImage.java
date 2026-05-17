package com.Ecommerce.CartService.DTOs.Response;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.UUID;

@Embeddable
@Data
public class ProductImage {
  private UUID id;
  private String imageUrl;
}
