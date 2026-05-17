package com.Ecommerce.OrderService.Client;

import com.Ecommerce.OrderService.DTOs.Response.ProductSummary;
import com.Ecommerce.OrderService.DTOs.Response.RestApiResponse;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

  @GetMapping("internal/product/{productId}/verify")
  RestApiResponse<Boolean> verifyProductExists(@PathVariable UUID productId);

  @GetMapping("internal/product/{productId}/summary")
  RestApiResponse<ProductSummary> getProductSummary(@PathVariable UUID productId);

  @PostMapping("internal/product/batch")
  RestApiResponse<List<ProductSummary>> getProductBatch(@RequestBody @NotEmpty Set<UUID> set);
}
