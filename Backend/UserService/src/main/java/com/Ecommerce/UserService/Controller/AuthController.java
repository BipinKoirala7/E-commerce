package com.Ecommerce.UserService.Controller;

import com.Ecommerce.UserService.DTOs.Request.UserCreateDTO;
import com.Ecommerce.UserService.DTOs.Request.UserLoginDTO;
import com.Ecommerce.UserService.DTOs.Response.RestApiResponse;
import com.Ecommerce.UserService.DTOs.Response.UserResponseDTO;
import com.Ecommerce.UserService.Service.AuthService;
import com.Ecommerce.UserService.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller handling user authentication for the UserService.
 *
 * <p>All endpoints are mounted under {@code /auth} and are publicly accessible
 * (no JWT required). Authentication state is communicated via HTTP-only cookies
 * set on the {@link HttpServletResponse} rather than response bodies, keeping
 * tokens out of JavaScript scope.</p>
 *
 * @see AuthService
 * @see UserCreateDTO
 * @see UserLoginDTO
 */
@RequestMapping("auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("register")
  public ResponseEntity<RestApiResponse<Void>> registerNewUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
    authService.registerUser(userCreateDTO);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(RestApiResponse.success(HttpStatus.CREATED.value(), "User Created!"));
  }

  @PostMapping("login")
  public ResponseEntity<RestApiResponse<Void>> login(@Valid @RequestBody UserLoginDTO userLoginDTO, HttpServletResponse response) {
    authService.loginUser(userLoginDTO, response);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), "Logged In!"));
  }

  @PostMapping("logout")
  public ResponseEntity<RestApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
    authService.logout(request, response);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), "Logged Out!"));
  }

}
