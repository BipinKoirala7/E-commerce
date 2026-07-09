package com.Ecommerce.UserService.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;

import com.Ecommerce.UserService.Model.ActiveRefreshToken;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ActiveRefreshTokenRepositoryTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine")
      .withDatabaseName("test_db")
      .withUsername("test_user")
      .withPassword("test_pass")
      .withReuse(true);

  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  private ActiveRefreshTokenRepository repository;

  private final ActiveRefreshToken activeRefreshToken = new ActiveRefreshToken();
  private final UUID userId = UUID.randomUUID();
  private final String refreshToken = "Refresh Token";

  @BeforeEach
  void beforeEach() {
    activeRefreshToken.setUserId(userId);
    activeRefreshToken.setRefreshToken(refreshToken);
    activeRefreshToken.setRevoked(false);
    activeRefreshToken.setExpiresAt(LocalDateTime.now());

    testEntityManager.persistAndFlush(activeRefreshToken);
  }

  @Test
  void shouldFindTokenById() {
    Optional<ActiveRefreshToken> result = repository.findById(activeRefreshToken.getId());

    assertTrue(result.isPresent());
    assertEquals(userId, result.get().getUserId());
    assertEquals(activeRefreshToken.getId(), result.get().getId());
    assertFalse(result.get().isRevoked());
    assertEquals("Refresh Token", result.get().getRefreshToken());
  }

  @Test
  void shouldReturnEmptyWhenTokenNotFound() {
    Optional<ActiveRefreshToken> result = repository.findById(UUID.randomUUID());
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldConfirmExistsByRefreshToken() {
    assertTrue(repository.existsByRefreshToken(refreshToken));
    assertFalse(repository.existsByRefreshToken("nonexistent-token"));
  }

  @Test
  void shouldUpdate() {
    activeRefreshToken.setRefreshToken("Updated Refresh Token");
    repository.save(activeRefreshToken);

    Optional<ActiveRefreshToken> result = repository.findById(activeRefreshToken.getId());

    assertEquals("Updated Refresh Token", result.get().getRefreshToken());
  }

  @Test
  void shouldDeleteByUserIdAndRefreshToken() {
    assertTrue(repository.existsById(activeRefreshToken.getId()));

    repository.deleteByUserIdAndRefreshToken(userId, activeRefreshToken.getRefreshToken());
    assertFalse(repository.existsById(activeRefreshToken.getId()));
  }

  @Test
  void shouldNotDeleteOtherUsersTokens() {
    UUID otherUserId = UUID.randomUUID();
    ActiveRefreshToken otherUserToken = new ActiveRefreshToken();
    otherUserToken.setUserId(otherUserId);
    otherUserToken.setRefreshToken("Other User Token");
    otherUserToken.setRevoked(false);
    otherUserToken.setExpiresAt(LocalDateTime.now());
    testEntityManager.persistAndFlush(otherUserToken);

    repository.deleteByUserId(userId);

    assertFalse(repository.existsById(activeRefreshToken.getId()));
    assertTrue(repository.existsById(otherUserToken.getId()));
  }

  @Test
  void shouldDeleteAllByUserId() {
    ActiveRefreshToken secondToken = new ActiveRefreshToken();
    secondToken.setUserId(userId);
    secondToken.setRefreshToken("Second Refresh Token");
    secondToken.setRevoked(false);
    secondToken.setExpiresAt(LocalDateTime.now());
    testEntityManager.persistAndFlush(secondToken);

    repository.deleteByUserId(userId);

    assertFalse(repository.existsById(activeRefreshToken.getId()));
    assertFalse(repository.existsById(secondToken.getId()));
  }

}
