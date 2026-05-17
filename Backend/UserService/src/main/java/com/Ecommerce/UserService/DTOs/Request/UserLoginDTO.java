package com.Ecommerce.UserService.DTOs.Request;

import lombok.Data;

@Data
public class UserLoginDTO {
  private String email;
  private String password;
}
