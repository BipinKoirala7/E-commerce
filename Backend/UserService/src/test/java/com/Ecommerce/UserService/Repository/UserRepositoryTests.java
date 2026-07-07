package com.Ecommerce.UserService.Repository;

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
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.Ecommerce.UserService.Model.AuthProvider;
import com.Ecommerce.UserService.Model.Role;
import com.Ecommerce.UserService.Model.User;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTests {

  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  private UserRepository underTest;

  private User user;
  private User oauthUser; 

  @BeforeEach
  void beforeEach() {
    user = new User();
    user.setUserName("bipinkoirala7");
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

    assertEquals(newLastLoginTime, result.get().getLastLoginAt());
  }

  @Test
  void shouldOnlyUpdateLastLoginDate_forSpecifiedUser() {
    LocalDateTime originalOauthLoginTime = oauthUser.getLastLoginAt()
        .truncatedTo(ChronoUnit.MICROS);
    LocalDateTime newLastLoginTime = LocalDateTime.now()
        .plusDays(1)
        .truncatedTo(ChronoUnit.MICROS);

    underTest.updateLastLoginDate(newLastLoginTime, user.getId());
    testEntityManager.flush();
    testEntityManager.clear();

    User updatedUser = underTest.findByEmail(user.getEmail()).get();
    User untouchedUser = underTest.findByEmail(oauthUser.getEmail()).get();

    assertEquals(newLastLoginTime, updatedUser.getLastLoginAt());
    assertEquals(originalOauthLoginTime, untouchedUser.getLastLoginAt());
  }

  @Test
  void shouldAffectNoRows_whenIdDoesNotExist() {
    LocalDateTime newLastLoginTime = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    UUID randomId = UUID.randomUUID();

    underTest.updateLastLoginDate(newLastLoginTime, randomId);
    testEntityManager.flush();
    testEntityManager.clear();

    User untouchedUser = underTest.findByEmail(user.getEmail()).get();
    assertNotEquals(newLastLoginTime, untouchedUser.getLastLoginAt());
  }
}