package com.Ecommerce.UserService.DTOs.Request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateDTO {

  @NotBlank
  private String userName;

  @NotBlank
  @Email
  private String email;

  @NotBlank
  private String password;

  @Nullable
  private String profilePictureUrl;
}
