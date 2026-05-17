package com.Ecommerce.ProductService.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "products")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @NotNull
  @Column(nullable = false)
  private String name;

  @NotNull
  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @NotNull
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @NotNull
  @Column(nullable = false)
  private Integer stockQuantity;

  @NotNull
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Category category;

  @NotNull
  @Column(nullable = false)
  private String imageUrl;

  @NotNull
  @Column(nullable = false)
  private String brand;

  @Column(nullable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(nullable = false)
  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
