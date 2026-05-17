package com.Ecommerce.APIGateway.Client;

import com.Ecommerce.APIGateway.DTOs.RestApiResponse;
import com.Ecommerce.APIGateway.DTOs.TokenDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserServiceClient {

  @PostMapping("internal/auth/token-refresh")
  public RestApiResponse<TokenDTO> refreshToken(@RequestBody @Valid String refreshToken);
}
