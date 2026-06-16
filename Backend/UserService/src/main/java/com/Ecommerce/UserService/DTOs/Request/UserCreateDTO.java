package com.Ecommerce.UserService.DTOs.Request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserCreateDTO {

  @NotNull
  private String userName;

  @NotNull
  private String email;

  @NotNull
  private String password;

  @Nullable
  private String profilePictureUrl;
}
