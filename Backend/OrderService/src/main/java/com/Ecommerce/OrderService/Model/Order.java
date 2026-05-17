package com.Ecommerce.OrderService.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @NotNull
  @Column(nullable = false)
  private UUID userId;

  @NotNull
  @Column(nullable = false)
  private String orderNumber;

  @NotNull
  @Column(nullable = false)
  private String billingAddress;

  @NotNull
  @Column(nullable = false)
  private String shippingAddress;

  @NotNull
  @Email
  @Column(nullable = false)
  private String email;

  @NotNull
  @Size(min = 7, max = 20)
  @Column(nullable = false)
  private String phone;

  @NotNull
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal totalPrice;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumn(name = "order_id")
  private List<OrderItem> orderItems = new ArrayList<>();

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus orderStatus;

  @CreationTimestamp
  @Column(nullable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  public void addOrderItem(@NonNull OrderItem item) {
    orderItems.add(item);
  }

}
