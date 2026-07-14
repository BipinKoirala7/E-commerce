package com.Ecommerce.UserService.DTOs.Request;

import com.Ecommerce.UserService.Model.Role;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserUpdateDTO {

  @Length(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
  private String userName;

  @Email
  private String email;

  private Boolean emailVerified;

  private Role role;

  private String profilePictureUrl;

}
