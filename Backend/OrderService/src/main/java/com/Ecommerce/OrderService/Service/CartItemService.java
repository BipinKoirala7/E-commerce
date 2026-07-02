package com.Ecommerce.OrderService.Service;

import com.Ecommerce.OrderService.Client.ProductServiceClient;
import com.Ecommerce.OrderService.DTOs.Request.CartItemUpdateDto;
import com.Ecommerce.OrderService.DTOs.Response.CartItemResponseDTO;
import com.Ecommerce.OrderService.DTOs.Response.ProductSummary;
import com.Ecommerce.OrderService.DTOs.Response.RestApiResponse;
import com.Ecommerce.OrderService.Exception.CartItemNotFound;
import com.Ecommerce.OrderService.Mapper.CartItemMapper;
import com.Ecommerce.OrderService.Model.CartItem;
import com.Ecommerce.OrderService.Repository.CartItemRepository;
import com.Ecommerce.OrderService.Security.SecurityUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Cart Item Service handles cart item creation and deletion.
 *
 * @see ProductServiceClient
 * @see CartItemRepository
 * @see CartItemMapper
 * @see CartItemUpdateDto
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemService {
  private final ProductServiceClient productServiceClient;
  private final CartItemRepository cartItemRepository;
  private final CartItemMapper cartItemMapper;

  @Transactional
  public void createNewCartItem(UUID productId) {
    log.info("Cart Item Creation");

    if (Objects.isNull(productId)) {
      log.warn("Cart Item Creation Failed - Product Id is null");
      throw new IllegalArgumentException("Product Id is null");
    }

    Optional<CartItem> existingCartItem = cartItemRepository.findByProductIdAndUserId(productId, SecurityUtils.getCurrentUserId());

    if (existingCartItem.isPresent()) {
      log.debug("Cart Item Creation Info - Cart Item already exists And Updating Cart Item Quantity");
      updateCartItemQuantity(existingCartItem.get().getProductId(), new CartItemUpdateDto(existingCartItem.get().getQuantity() + 1));
      return;
    }

    CartItem newCartItem = cartItemMapper.toCartItemEntity(productId);
    newCartItem.setUserId(SecurityUtils.getCurrentUserId());

    cartItemRepository.save(newCartItem);
    log.info("Cart Item Creation Success");
  }

  public List<CartItemResponseDTO> getAllCartItems() {
    log.info("Fetching Cart Items");

    List<CartItem> cartItems = cartItemRepository.findByUserId(SecurityUtils.getCurrentUserId());

    return cartItems.stream().map(cartItem -> {
      RestApiResponse<ProductSummary> apiResponse = productServiceClient.getProductSummary(cartItem.getProductId());

      if (!apiResponse.getSuccess()) {
        log.debug("CartItem Product Summary Retrieval Failed");
        throw new IllegalArgumentException("Product Summary Retrieval Failed");
      }

      ProductSummary productSummary = apiResponse.getData();
      return new CartItemResponseDTO(cartItem, productSummary);

    }).toList();
  }

  @Transactional
  public void updateCartItemQuantity(@NotNull UUID productId, @NonNull CartItemUpdateDto cartItemUpdateDTO) {
    log.info("Updating Cart Item...");

    if (!cartItemRepository.existsByProductIdAndUserId(productId, SecurityUtils.getCurrentUserId())) {
      log.warn("Cart Item Update Failed - Cart Item doesn't exists");
      throw new CartItemNotFound("Cart Item doesn't exists");
    }

    if (cartItemUpdateDTO.getQuantity() < 0) {
      log.warn("Cart Item Update Failed - Quantity must be greater than zero");
      throw new IllegalArgumentException("Quantity must be greater than zero");
    }

    if (cartItemUpdateDTO.getQuantity() == 0) {
      log.debug("Updated Quantity is zero, Deleting Cart Item");
      deleteCartItem(productId);
      return;
    }
    log.debug("Updating Cart Item Info - Cart Item exists and Updated Quantity is valid");

    cartItemRepository.updateQuantityByIdAndUserId(cartItemUpdateDTO.getQuantity(), productId, SecurityUtils.getCurrentUserId());
    log.info("Cart Item Update Success");
  }

  @Transactional
  public void deleteCartItem(UUID productId) {
    log.info("Cart Item Deletion...");

    if (Objects.isNull(productId)) {
      log.warn("Cart Item Deletion Failed - Product Id is null");
      throw new IllegalArgumentException("Product Id is null");
    }

    if (!cartItemRepository.existsByProductIdAndUserId(productId, SecurityUtils.getCurrentUserId())) {
      log.warn("Cart Item Deletion Failed - Cart Item doesn't exists");
      throw new CartItemNotFound("Cart Item doesn't exists");
    }

    cartItemRepository.deleteByProductIdAndUserId(productId, SecurityUtils.getCurrentUserId());
    log.info("Cart Item Deletion Success");
  }
}
