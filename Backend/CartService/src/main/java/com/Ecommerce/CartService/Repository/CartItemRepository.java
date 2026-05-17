package com.Ecommerce.CartService.Repository;

import com.Ecommerce.CartService.Model.CartItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

  List<CartItem> findByUserId(UUID userId);
  Optional<CartItem> findByProductIdAndUserId(UUID productId, UUID userId);
  Optional<CartItem> findByIdAndUserId(UUID id, UUID userId);

  @Modifying
  @Transactional
  @Query(value = "UPDATE cart_item SET quantity = :quantity WHERE product_id = :productId AND user_id = :userId", nativeQuery = true)
  void updateQuantityByIdAndUserId(Integer quantity, UUID productId, UUID userId);

  boolean existsByProductIdAndUserId(UUID productId, UUID userId);
  void deleteByProductIdAndUserId(UUID productId, UUID userId);
}
