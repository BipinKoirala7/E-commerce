package com.Ecommerce.CartService.Client;

import com.Ecommerce.CartService.DTOs.Response.ProductSummary;
import com.Ecommerce.CartService.DTOs.Response.RestApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

  @GetMapping("internal/product/{productId}/summary")
  RestApiResponse<ProductSummary> getProductSummary(@PathVariable UUID productId);

  @GetMapping("internal/product/{productId}/verify")
  RestApiResponse<Boolean> verifyProductExists(@PathVariable UUID productId);
}
