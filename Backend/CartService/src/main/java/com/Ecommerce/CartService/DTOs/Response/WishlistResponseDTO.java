package com.Ecommerce.CartService.DTOs.Response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class WishlistResponseDTO {

  @NotNull
  private UUID id;

  @NotNull
  private ProductSummary product;

  @NotNull
  private LocalDateTime createdAt;
}
