package com.Ecommerce.UserService.Repository;

import com.Ecommerce.UserService.Model.ActiveRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ActiveRefreshTokenRepository extends JpaRepository<ActiveRefreshToken, UUID> {

  void deleteByUserIdAndRefreshToken(UUID userId, String refreshToken);
  void deleteByUserId(UUID userId);

  boolean existsByRefreshToken(String refreshToken);
}
