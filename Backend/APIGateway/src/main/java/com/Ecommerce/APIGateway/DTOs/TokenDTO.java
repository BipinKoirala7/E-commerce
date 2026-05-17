package com.Ecommerce.APIGateway.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenDTO {
  String refreshToken;
  String accessToken;
}
