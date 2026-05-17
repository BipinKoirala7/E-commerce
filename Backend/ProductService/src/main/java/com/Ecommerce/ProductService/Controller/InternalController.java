package com.Ecommerce.ProductService.Controller;

import com.Ecommerce.ProductService.DTOs.Response.ProductSummary;
import com.Ecommerce.ProductService.DTOs.Response.RestApiResponse;
import com.Ecommerce.ProductService.Service.ProductService;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Takes requests from other microservices.
 *
 * @see ProductService
 * */
@RestController
@RequestMapping("internal/product")
@RequiredArgsConstructor
public class InternalController {
  private final ProductService productService;

  @GetMapping("{productId}/summary")
  public ResponseEntity<RestApiResponse<ProductSummary>> getProductSummary(@PathVariable UUID productId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), productService.getProductSummary(productId), "Product exists"));
  }

  @GetMapping("{productId}/verify")
  public ResponseEntity<RestApiResponse<Boolean>> verifyProductExists(@PathVariable UUID productId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), productService.verifyProductExists(productId), "Product exists"));
  }

  @PostMapping("batch")
  public ResponseEntity<RestApiResponse<List<ProductSummary>>> getProductBatch(@RequestBody @NotEmpty Set<UUID> set){
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), productService.getProductBatch(set), "Product Fetched SuccessFully"));
  }
}
