package com.Ecommerce.CartService.DTOs.Response;

import com.Ecommerce.CartService.Model.CartItem;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CartItemResponseDTO {

  public CartItemResponseDTO(@NonNull CartItem cartItem, ProductSummary productSummary){
    this.id = cartItem.getId();
    this.productSummary = productSummary;
    this.quantity = cartItem.getQuantity();
    this.createdAt = cartItem.getCreatedAt();
    this.updatedAt = cartItem.getUpdatedAt();
  }

  @NotNull
  private UUID id;

  @NotNull
  private ProductSummary productSummary;

  @NotNull
  private Integer quantity;

  @NotNull
  private LocalDateTime createdAt;

  @NotNull
  private LocalDateTime updatedAt;
}
