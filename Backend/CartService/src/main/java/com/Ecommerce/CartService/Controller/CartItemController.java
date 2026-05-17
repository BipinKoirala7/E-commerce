package com.Ecommerce.CartService.Controller;

import com.Ecommerce.CartService.DTOs.Request.CartItemUpdateDTO;
import com.Ecommerce.CartService.DTOs.Response.CartItemResponseDTO;
import com.Ecommerce.CartService.DTOs.Response.RestApiResponse;
import com.Ecommerce.CartService.Service.CartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("cart-item")
@RequiredArgsConstructor
public class CartItemController {

  private final CartItemService cartItemService;

  @PostMapping("{productId}")
  public ResponseEntity<RestApiResponse<Void>> createCartItem(@PathVariable @Valid UUID productId) {
    cartItemService.createNewCartItem(productId);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(RestApiResponse.success(HttpStatus.CREATED.value(), "Successfully Created Cart Item."));
  }

  @GetMapping
  public ResponseEntity<RestApiResponse<List<CartItemResponseDTO>>> getCartItems() {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), cartItemService.getAllCartItems(), "Successfully Fetched Items of a Cart"));
  }

  @PatchMapping("{productId}")
  public ResponseEntity<RestApiResponse<Void>> updateCartItem(@Valid @RequestBody CartItemUpdateDTO cartItemUpdateDTO, @PathVariable UUID productId) {
    cartItemService.updateCartItemQuantity(productId, cartItemUpdateDTO);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), "Successfully Updated CartItem"));
  }

  @DeleteMapping("{productId}")
  public ResponseEntity<RestApiResponse<Void>> deleteCartItem(@PathVariable @Valid UUID productId) {
    cartItemService.deleteCartItem(productId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), "Successfully Deleted CartItem"));
  }
}
