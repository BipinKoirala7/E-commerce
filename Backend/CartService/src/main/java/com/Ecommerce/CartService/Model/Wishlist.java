package com.Ecommerce.CartService.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "wishlists")
@NoArgsConstructor
public class Wishlist {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public UUID id;

  @NotNull
  @Column(nullable = false)
  private UUID userId;

  @NotNull
  @Column(nullable = false)
  private UUID productId;

  @CreationTimestamp
  @Column(nullable = false)
  private LocalDateTime createdAt;
}
