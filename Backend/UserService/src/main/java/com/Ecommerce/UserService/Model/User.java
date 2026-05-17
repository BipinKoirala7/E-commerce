package com.Ecommerce.UserService.Model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @NotNull
  @Column(nullable = false)
  private String userName;

  @NotNull
  @Email
  @Column(nullable = false)
  private String email;

  @Nullable
  private String password;

  @Enumerated(EnumType.STRING)
  @NotNull
  @Column(nullable = false)
  private AuthProvider authProvider;

  @Nullable
  private String providerId;

  @NotNull
  @Column(nullable = false)
  private Boolean emailVerified;

  @Enumerated(EnumType.STRING)
  @NotNull
  @Column(nullable = false)
  private Role role;

  private String profilePictureUrl;

  @Nullable
  @PastOrPresent
  @Column(nullable = false)
  private LocalDateTime lastLoginAt;

  @CreationTimestamp
  @Column(nullable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime updatedAt;
}
