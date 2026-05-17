package com.Ecommerce.UserService.Repository;

import com.Ecommerce.UserService.Model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);

  @Modifying
  @Transactional
  @Query(value = "UPDATE users SET last_login_at = :lastLoginAt WHERE id = :id", nativeQuery = true)
  void updateLastLoginDate(@Param("lastLoginAt") LocalDateTime localDateTime, @Param("id") UUID id);

  boolean existsByEmail(String email);

  boolean existsByEmailAndProviderId(String email, String providerId);
}
