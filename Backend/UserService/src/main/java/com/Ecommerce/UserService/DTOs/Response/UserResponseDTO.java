package com.Ecommerce.UserService.DTOs.Response;

import com.Ecommerce.UserService.Model.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDTO {

  private String userName;
  private String email;
  private Boolean emailVerified;
  private Role role;
  private String profilePictureUrl;
  private LocalDateTime lastLoginAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
