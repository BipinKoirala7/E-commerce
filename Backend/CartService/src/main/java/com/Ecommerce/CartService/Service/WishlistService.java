package com.Ecommerce.CartService.Service;

import com.Ecommerce.CartService.Client.ProductServiceClient;
import com.Ecommerce.CartService.DTOs.Response.ProductSummary;
import com.Ecommerce.CartService.DTOs.Response.RestApiResponse;
import com.Ecommerce.CartService.DTOs.Response.WishlistResponseDTO;
import com.Ecommerce.CartService.Exception.ProductAlreadyInWishlistException;
import com.Ecommerce.CartService.Mapper.WishlistMapper;
import com.Ecommerce.CartService.Model.Wishlist;
import com.Ecommerce.CartService.Repository.WishlistRepository;
import com.Ecommerce.CartService.Security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Handles wishlist related services like creation, fetch,
 * update, and deletion.
 *
 * @see WishlistRepository
 * @see WishlistMapper
 * @see ProductServiceClient
 * */

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistService {
  private final WishlistMapper wishlistMapper;
  private final WishlistRepository wishlistRepository;
  private final ProductServiceClient productServiceClient;

  @Transactional
  public void addToWishlist(UUID productId) {
    log.info("Add Product to Wishlist");

    if (Objects.isNull(productId)) {
      log.debug("Add Product to Wishlist Failed: Wishlist cannot be null");
      throw new IllegalArgumentException("Product Id cannot be null");
    }
    log.debug("Add Product to Wishlist Info: Product Id is present");

    if (isProductInWishlist(productId, SecurityUtils.getCurrentUserId())) {
      log.debug("Add Product to Wishlist Failed: Product is already in wishlist for the user");
      throw new ProductAlreadyInWishlistException("Product already in wishlist");
    }
    log.debug("Add Product to Wishlist Info: Product is not in wishlist for the user, proceeding to add");

    Wishlist newWishlist = wishlistMapper.toWishlistEntity(productId);
    newWishlist.setUserId(SecurityUtils.getCurrentUserId());
    log.debug("Add Product to Wishlist Info: Wishlist Entity created successfully");

    wishlistRepository.save(newWishlist);
    log.info("Add Product to Wishlist Success");
  }

  public List<WishlistResponseDTO> getWishlist() {
    log.info("Fetching Wishlist Products...");

    List<Wishlist> wishLists = wishlistRepository.findByUserId(SecurityUtils.getCurrentUserId());
    log.debug("Wishlist Products Retrieved Successfully");

    return wishLists.stream().map(wishlist -> {
      RestApiResponse<ProductSummary> apiResponse = productServiceClient.getProductSummary(wishlist.getProductId());

      if(!apiResponse.getSuccess()){
        log.debug("Wishlist Product Summary Retrieval Failed ");
        throw new IllegalArgumentException("Product Summary Retrieval Failed"); //  Maybe some other exception
      }

      ProductSummary productSummary = apiResponse.getData();
      return new WishlistResponseDTO(wishlist.getId(), productSummary, wishlist.getCreatedAt());
    }).toList();
  }

  @Transactional
  public void removeFromWishlist(UUID productId) {
    log.info("Remove Product from Wishlist");

    if (Objects.isNull(productId)) {
      log.warn("Remove Product from Wishlist Failed: Product Id cannot be null");
      throw new IllegalArgumentException("Wishlist Id cannot be null");
    }
    log.debug("Remove Product from Wishlist Info: Product Id is present");

    if(!isProductInWishlist(productId, SecurityUtils.getCurrentUserId())){
      log.debug("Remove Product from Wishlist Failed: Wishlist item does not exist for the user");
      throw new IllegalArgumentException("Wishlist item does not exist");
    }
    log.debug("Remove Product from Wishlist Info: Wishlist item exists for the user, proceeding to remove");

    wishlistRepository.deleteByProductIdAndUserId(productId, SecurityUtils.getCurrentUserId());
    log.info("Remove Product from Wishlist Success");
  }

  //  Utility Methods
  public boolean isProductInWishlist(UUID productId, UUID userId) {
    return wishlistRepository.existsByProductIdAndUserId(productId, userId);
  }
}
