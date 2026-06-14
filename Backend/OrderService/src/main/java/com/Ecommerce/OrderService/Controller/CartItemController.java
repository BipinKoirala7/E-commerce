package com.Ecommerce.OrderService.Controller;

import com.Ecommerce.OrderService.DTOs.Request.CartItemUpdateDto;
import com.Ecommerce.OrderService.DTOs.Response.CartItemResponseDTO;
import com.Ecommerce.OrderService.Service.CartItemService;
import com.Ecommerce.OrderService.DTOs.Response.RestApiResponse;
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
  public ResponseEntity<RestApiResponse<Void>> updateCartItem(@Valid @RequestBody CartItemUpdateDto cartItemUpdateDTO, @PathVariable UUID productId) {
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
