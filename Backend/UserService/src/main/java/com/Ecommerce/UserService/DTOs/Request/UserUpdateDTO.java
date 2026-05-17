package com.Ecommerce.UserService.DTOs.Request;

import com.Ecommerce.UserService.Model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserUpdateDTO {

  @NotNull
  private String userName;

  @NotNull
  private String email;

  @NotNull
  private Boolean emailVerified;

  @NotNull
  private Role role;

  private String profilePictureUrl;

}
