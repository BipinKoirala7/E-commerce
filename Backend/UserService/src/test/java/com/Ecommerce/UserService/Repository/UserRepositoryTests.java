package com.Ecommerce.UserService.Repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;

import com.Ecommerce.UserService.Model.AuthProvider;
import com.Ecommerce.UserService.Model.Role;
import com.Ecommerce.UserService.Model.User;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTests {

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
  private UserRepository underTest;

  private User user;
  private User oauthUser; 

  @BeforeEach
  void beforeEach() {
    user = new User();
    user.setUserName("bipin.koirala7");
    user.setEmail("bipin@example.com");
    user.setPassword("BipinPass!123");
    user.setAuthProvider(AuthProvider.LOCAL);
    user.setProviderId(null);
    user.setEmailVerified(false);
    user.setRole(Role.USER);
    user.setProfilePictureUrl(null);
    user.setLastLoginAt(LocalDateTime.now());

    oauthUser = new User();
    oauthUser.setUserName("bipin-google");
    oauthUser.setEmail("bipin.oauth@example.com");
    oauthUser.setPassword(null);
    oauthUser.setAuthProvider(AuthProvider.GOOGLE);
    oauthUser.setProviderId("google-oauth2|123456");
    oauthUser.setEmailVerified(true);
    oauthUser.setRole(Role.USER);
    oauthUser.setProfilePictureUrl(null);
    oauthUser.setLastLoginAt(LocalDateTime.now());

    testEntityManager.persistAndFlush(user);
    testEntityManager.persistAndFlush(oauthUser);
  }

  @Test
  void shouldFindByEmail() {
    Optional<User> result = underTest.findByEmail(user.getEmail());

    assertTrue(result.isPresent());
    assertEquals(user.getEmail(), result.get().getEmail());
  }

  @Test
  void shouldNotFindByEmail_whenEmailDoesNotExist() {
    Optional<User> result = underTest.findByEmail("random@example.com");

    assertFalse(result.isPresent());
  }

  @Test
  void shouldNotFindByEmail_whenCaseDiffers() {
    Optional<User> result = underTest.findByEmail("BIPIN@example.com");

    assertFalse(result.isPresent());
  }

  @Test
  void shouldConfirmExistsByEmail() {
    assertTrue(underTest.existsByEmail(user.getEmail()));
  }

  @Test
  void shouldNotExistByEmail_whenNotFound() {
    assertFalse(underTest.existsByEmail("nonexistent@example.com"));
  }

  @Test
  void shouldReturnTrue_whenEmailAndProviderIdBothMatch() {
    assertTrue(underTest.existsByEmailAndProviderId(
        oauthUser.getEmail(), oauthUser.getProviderId()));
  }

  @Test
  void shouldReturnFalse_whenEmailMatchesButProviderIdDoesNot() {
    assertFalse(underTest.existsByEmailAndProviderId(
        oauthUser.getEmail(), "wrong-provider-id"));
  }

  @Test
  void shouldReturnFalse_whenProviderIdMatchesButEmailDoesNot() {
    assertFalse(underTest.existsByEmailAndProviderId(
        "wrong@example.com", oauthUser.getProviderId()));
  }

  @Test
  void shouldReturnTrue_whenEmailMatchesAndProviderIdIsNull() {
    assertTrue(underTest.existsByEmailAndProviderId(user.getEmail(), null));
  }

  @Test
  void shouldReturnFalse_whenProviderIdIsNullButUserHasProviderId() {
    assertFalse(underTest.existsByEmailAndProviderId(oauthUser.getEmail(), null));
  }

  @Test
  void shouldUpdateLastLoginDate() {
    LocalDateTime newLastLoginTime = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

    underTest.updateLastLoginDate(newLastLoginTime, user.getId());
    testEntityManager.flush();
    testEntityManager.clear();

    Optional<User> result = underTest.findByEmail(user.getEmail());

    assertTrue(result.isPresent());
    assertEquals(newLastLoginTime, result.get().getLastLoginAt());
  }

  @Test
  void shouldOnlyUpdateLastLoginDate_forSpecifiedUser() {
    assert oauthUser.getLastLoginAt() != null;
    LocalDateTime originalOauthLoginTime = oauthUser.getLastLoginAt()
        .truncatedTo(ChronoUnit.MICROS);
    LocalDateTime newLastLoginTime = LocalDateTime.now()
        .plusDays(1)
        .truncatedTo(ChronoUnit.MICROS);

    underTest.updateLastLoginDate(newLastLoginTime, user.getId());
    testEntityManager.flush();
    testEntityManager.clear();

    Optional<User> updatedUser = underTest.findByEmail(user.getEmail());
    Optional<User> untouchedUser = underTest.findByEmail(oauthUser.getEmail());

    assertTrue(updatedUser.isPresent());
    assertTrue(untouchedUser.isPresent());
    assertEquals(newLastLoginTime, updatedUser.get().getLastLoginAt());
    assertThat(originalOauthLoginTime).isCloseTo(untouchedUser.get().getLastLoginAt(), within(2, ChronoUnit.MICROS));
  }

  @Test
  void shouldAffectNoRows_whenIdDoesNotExist() {
    LocalDateTime newLastLoginTime = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    UUID randomId = UUID.randomUUID();

    underTest.updateLastLoginDate(newLastLoginTime, randomId);
    testEntityManager.flush();
    testEntityManager.clear();

    Optional<User> untouchedUser = underTest.findByEmail(user.getEmail());
    assertTrue(untouchedUser.isPresent());
    assertNotEquals(newLastLoginTime, untouchedUser.get().getLastLoginAt());
  }
}