package com.Ecommerce.ProductService.Controller;

import com.Ecommerce.ProductService.DTOs.Response.PageResponse;
import com.Ecommerce.ProductService.DTOs.Response.RestApiResponse;
import com.Ecommerce.ProductService.Model.Category;
import com.Ecommerce.ProductService.Model.Product;
import com.Ecommerce.ProductService.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for Products.
 *
 * @see ProductService
 *
 */
@RestController
@RequestMapping("product")
@RequiredArgsConstructor
public class ProductController {
  private final ProductService productService;

  @GetMapping("category/{category}")
  public ResponseEntity<RestApiResponse<PageResponse<Product>>> getProductsByCategory(
      @PathVariable String category,
      @PageableDefault(size = 4, direction = Sort.Direction.ASC) Pageable pageable
  ) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), productService.getProductsByCategory(Category.fromString(category), pageable), "Successfully Fetched Products"));
  }

  @GetMapping("brand/{brand}")
  public ResponseEntity<RestApiResponse<PageResponse<Product>>> getProductsByBrand(
      @PathVariable String brand,
      @PageableDefault(size = 4, direction = Sort.Direction.ASC) Pageable pageable
  ) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), productService.getProductsByBrand(brand, pageable), "Successfully Fetched Products"));
  }

  //  I can and should be able to use this for every kind of product query.
  @GetMapping
  public ResponseEntity<RestApiResponse<PageResponse<Product>>> getAllProducts(
      @RequestParam(name = "query", defaultValue = "", required = false) String query,
      @RequestParam(name = "category", defaultValue = "", required = false) String category,
      @RequestParam(name = "minPrice", defaultValue = "0", required = false) String minPrice,
      @RequestParam(name = "maxPrice", defaultValue = "0", required = false) String maxPrice,
      @RequestParam(name = "page", defaultValue = "0", required = false) int page,
      @RequestParam(name = "size", defaultValue = "32", required = false) String size,
      @RequestParam(name = "sort", defaultValue = "createdAt", required = false) String sort,
      @RequestParam(name = "direction", defaultValue = "ASC", required = false) String direction
  ) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), productService.getProducts(query, category, Integer.parseInt(minPrice), Integer.parseInt(maxPrice), page, size, sort, direction), "Successfully Fetched Products"));
  }

  @GetMapping("{productId}")
  public ResponseEntity<RestApiResponse<Product>> getProductInfo(@PathVariable UUID productId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), productService.getProduct(productId), "Successfully Fetched Product Details"));
  }
}
