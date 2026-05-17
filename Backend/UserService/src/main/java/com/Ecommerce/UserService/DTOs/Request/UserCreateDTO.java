package com.Ecommerce.UserService.DTOs.Request;

import com.Ecommerce.UserService.Model.AuthProvider;
import com.Ecommerce.UserService.Model.Role;
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

  @NotNull
  private String profilePictureUrl;
}
