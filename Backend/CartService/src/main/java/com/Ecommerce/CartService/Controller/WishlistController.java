package com.Ecommerce.CartService.Controller;

import com.Ecommerce.CartService.DTOs.Response.RestApiResponse;
import com.Ecommerce.CartService.DTOs.Response.WishlistResponseDTO;
import com.Ecommerce.CartService.Service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {
  private final WishlistService wishlistService;

  @PostMapping("{productId}")
  public ResponseEntity<RestApiResponse<Void>> addToWishlist(@PathVariable @Valid UUID productId) {
    wishlistService.addToWishlist(productId);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(RestApiResponse.success(HttpStatus.CREATED.value(), "Successfully added to Wishlist"));
  }

  @GetMapping
  public ResponseEntity<RestApiResponse<List<WishlistResponseDTO>>> getUserWishlist() {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), wishlistService.getWishlist(), "Successfully retrieved Wishlist"));
  }

  @DeleteMapping("{productId}")
  public ResponseEntity<RestApiResponse<Void>> removeFromWishlist(@PathVariable UUID productId) {
    wishlistService.removeFromWishlist(productId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), "Successfully removed from Wishlist"));
  }
}
