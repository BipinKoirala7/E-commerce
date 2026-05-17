package com.Ecommerce.UserService.Controller;

import com.Ecommerce.UserService.DTOs.Request.UserUpdateDTO;
import com.Ecommerce.UserService.DTOs.Response.RestApiResponse;
import com.Ecommerce.UserService.DTOs.Response.UserResponseDTO;
import com.Ecommerce.UserService.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller handling user endpoints for User Service
 *
 * <p>All the endpoints are mapped under {@code /user} and aren't
 * publicly accessible. Authentication is required.</p>
 *
 * @see UserService
 * @see UserUpdateDTO
 * @see RestApiResponse
 * **/
@RequestMapping("user")
@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping
  public ResponseEntity<RestApiResponse<UserResponseDTO>> getUserInfo() {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), userService.getUser(), "User Info Fetched Successfully"));
  }

  @PatchMapping
  public ResponseEntity<RestApiResponse<Void>> updateUserInfo(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
    userService.updateUser(userUpdateDTO);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), "Details Updated!"));
  }

  @DeleteMapping
  public ResponseEntity<RestApiResponse<Void>> deleteUser() {
    userService.deleteUser();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), "Account Deleted!"));
  }
}
