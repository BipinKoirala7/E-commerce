package com.Ecommerce.UserService.DTOs.Request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OAuthUserCreateDTO {

  @NotNull
  private String userName;

  @NotNull
  private String email;

  @NotNull
  private String providerId;

  @NotNull
  private String profilePictureUrl;
}
