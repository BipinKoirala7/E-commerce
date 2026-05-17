package com.Ecommerce.CartService.Service;

import com.Ecommerce.CartService.Client.ProductServiceClient;
import com.Ecommerce.CartService.DTOs.Request.CartItemUpdateDTO;
import com.Ecommerce.CartService.DTOs.Response.CartItemResponseDTO;
import com.Ecommerce.CartService.DTOs.Response.ProductSummary;
import com.Ecommerce.CartService.DTOs.Response.RestApiResponse;
import com.Ecommerce.CartService.Exception.CartItemNotFound;
import com.Ecommerce.CartService.Mapper.CartItemMapper;
import com.Ecommerce.CartService.Model.CartItem;
import com.Ecommerce.CartService.Repository.CartItemRepository;
import com.Ecommerce.CartService.Security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Cart Item Service handles cart item creation and deletion.
 *
 * @see ProductServiceClient
 * @see CartItemRepository
 * @see CartItemMapper
 * @see CartItemUpdateDTO
 * */
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
    log.debug("Cart Item Creation Info - Product Id is present");

    Optional<CartItem> existingCartItem = cartItemRepository
        .findByProductIdAndUserId(productId, SecurityUtils.getCurrentUserId());

    if (existingCartItem.isPresent()) {
      log.debug("Cart Item Creation Info - Cart Item already exists And Updating Cart Item Quantity");
      updateCartItemQuantity(existingCartItem.get().getId(), new CartItemUpdateDTO(existingCartItem.get().getQuantity() + 1));

      log.info("Cart Item Creation Success");
      return;
    }

    log.debug("Cart Item Creation Info - Cart Item doesn't exists");
    CartItem newCartItem = cartItemMapper.toCartItemEntity(productId);
    newCartItem.setUserId(SecurityUtils.getCurrentUserId());

    cartItemRepository.save(newCartItem);
    log.info("Cart Item Creation Success");
  }

  public List<CartItemResponseDTO> getAllCartItems() {
    log.info("Fetching Cart Items");

    List<CartItem> cartItems = cartItemRepository.findByUserId(SecurityUtils.getCurrentUserId());
    log.info("Cart Items Retrieval Success");

    return cartItems.stream().map(cartItem -> {
      RestApiResponse<ProductSummary> apiResponse = productServiceClient.getProductSummary(cartItem.getProductId());

      if(!apiResponse.getSuccess()){
        log.debug("CartItem Product Summary Retrieval Failed");
        throw new IllegalArgumentException("Product Summary Retrieval Failed"); //  Maybe some other exception
      }

      ProductSummary productSummary = apiResponse.getData();
      return new CartItemResponseDTO(cartItem, productSummary);

    }).toList();
  }

  @Transactional
  public void updateCartItemQuantity(UUID productId, CartItemUpdateDTO cartItemUpdateDTO) {
    log.info("Updating Cart Item...");

    if (Objects.isNull(productId)) {
      log.warn("Cart Item Update Failed - Product Id is null");
      throw new IllegalArgumentException("Product Id  is null");
    }
    log.debug("Updating Cart Item Info - Product Id is present");

    if (Objects.isNull(cartItemUpdateDTO)) {
      log.warn("Cart Item Update Failed - Updated Cart Item is null");
      throw new IllegalArgumentException("Updated Cart Item is null");
    }
    log.debug("Updating Cart Item Info - Updated Cart Item is present");

    log.debug("ProductId: {}, Updated Quantity: {}", productId, cartItemUpdateDTO.getQuantity());
    if(!cartItemRepository.existsByProductIdAndUserId(productId, SecurityUtils.getCurrentUserId())){
      log.warn("Cart Item Update Failed - Cart Item doesn't exists");
      throw new CartItemNotFound("Cart Item doesn't exists");
    }

    if(cartItemUpdateDTO.getQuantity() <= 0){
      log.warn("Cart Item Update Failed - Quantity must be greater than zero");
      throw new IllegalArgumentException("Quantity must be greater than zero");
    }
    log.debug("Updating Cart Item Info - Cart Item exists and Updated Quantity is valid");

    //  Using custom query to update quantity directly in the database without fetching the entity first
    //  Make a decision whether to have updated_at or not
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
    log.debug("Cart Item Deletion Info - Product Id is present");

    if (!cartItemRepository.existsByProductIdAndUserId(productId, SecurityUtils.getCurrentUserId())) {
      log.warn("Cart Item Deletion Failed - Cart Item doesn't exists");
      throw new CartItemNotFound("Cart Item doesn't exists");
    }
    log.debug("Cart Item Deletion Info - Cart Item exists");

    cartItemRepository.deleteByProductIdAndUserId(productId, SecurityUtils.getCurrentUserId());
    log.info("Cart Item Deletion Success");
  }
}
