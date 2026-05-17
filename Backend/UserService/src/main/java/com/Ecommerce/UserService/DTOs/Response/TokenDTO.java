package com.Ecommerce.UserService.DTOs.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenDTO {
  private String refreshToken;
  private String accessToken;
}
