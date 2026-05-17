package com.Ecommerce.UserService.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "active_refresh_token")
@Data
public class ActiveRefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @NotNull
  @Column(nullable = false)
  private UUID userId;

  @NotNull
  @Column(nullable = false, unique = true, columnDefinition = "TEXT")
  private String refreshToken;

  @NotNull
  @Column(nullable = false)
  private LocalDateTime expiresAt;

  @NotNull
  @Column(nullable = false)
  private boolean revoked;

  @CreationTimestamp
  @Column(nullable = false)
  private LocalDateTime createdAt;
}
