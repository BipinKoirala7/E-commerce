package com.Ecommerce.UserService.Repository;

import com.Ecommerce.UserService.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);

  Optional<User> findByIdAndEmail(UUID id, String email);

  @Modifying
  @Transactional
  @Query(value = "UPDATE users SET last_login_at = :lastLoginAt WHERE id = :id", nativeQuery = true)
  void updateLastLoginDate(@Param("lastLoginAt") LocalDateTime localDateTime, @Param("id") UUID id);

  boolean existsByEmail(String email);

  boolean existsByEmailAndProviderId(String email, String providerId);

  @Transactional
  void deleteByEmail(String email);
}
