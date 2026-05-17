package com.Ecommerce.CartService.Repository;

import com.Ecommerce.CartService.Model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
  List<Wishlist> findByUserId(UUID userId);

  boolean existsByProductIdAndUserId(UUID productId, UUID userId);

  void deleteByProductIdAndUserId(UUID productId, UUID userId);
}
