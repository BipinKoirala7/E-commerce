package com.Ecommerce.UserService.Controller;

import com.Ecommerce.UserService.DTOs.Response.RestApiResponse;
import com.Ecommerce.UserService.DTOs.Response.TokenDTO;
import com.Ecommerce.UserService.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalController {

  private final AuthService authService;

  @PostMapping("auth/token-refresh")
  public ResponseEntity<RestApiResponse<TokenDTO>> refreshToken(@RequestBody @Valid String refreshToken){
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), authService.refreshTokens(refreshToken), "Successfully generated refreshed tokens"));
  }
}
