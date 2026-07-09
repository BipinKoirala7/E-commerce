package com.Ecommerce.UserService.Service;

import com.Ecommerce.UserService.DTOs.Response.TokenDTO;
import com.Ecommerce.UserService.Exception.EmptyTokenException;
import com.Ecommerce.UserService.Exception.InValidTokenException;
import com.Ecommerce.UserService.Model.ActiveRefreshToken;
import com.Ecommerce.UserService.Model.Role; // adjust if your enum lives elsewhere
import com.Ecommerce.UserService.Model.User;
import com.Ecommerce.UserService.Repository.ActiveRefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTests {

  @Mock
  private ActiveRefreshTokenRepository activeRefreshTokenRepository;

  @InjectMocks
  private JwtService jwtService;

  @Captor
  private ArgumentCaptor<ActiveRefreshToken> activeRefreshTokenArgumentCaptor;

  // 32+ bytes so Keys.hmacShaKeyFor doesn't throw WeakKeyException
  private static final String TEST_SIGNATURE_KEY =
      Encoders.BASE64.encode("test-signature-key-must-be-32-bytes-min!".getBytes(StandardCharsets.UTF_8));
  private static final String ISSUER = "api.ecommerce.com";
  private static final String AUDIENCE = "ecommerce.com";
  private static final long ACCESS_TOKEN_EXPIRATION = 3600L;
  private static final long REFRESH_TOKEN_EXPIRATION = 259200L;

  private User testUser;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(jwtService, "APPLICATION_SIGNATURE", TEST_SIGNATURE_KEY);
    ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", ACCESS_TOKEN_EXPIRATION);
    ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", REFRESH_TOKEN_EXPIRATION);
    ReflectionTestUtils.setField(jwtService, "AUDIENCE", AUDIENCE);
    ReflectionTestUtils.setField(jwtService, "ISSUER", ISSUER);

    testUser = new User();
    testUser.setId(UUID.randomUUID());
    testUser.setEmail("test@example.com");
    testUser.setRole(Role.USER);
  }

  @Nested
  class TokenGeneration {

    @Test
    void generateRefreshToken_containsExpectedClaims() {
      String token = jwtService.generateRefreshToken(testUser);

      Claims claims = jwtService.extractClaims(token);
      assertThat(claims.getSubject()).isEqualTo(testUser.getId().toString());
      assertThat(claims.get("tokenType", String.class)).isEqualTo("REFRESH_TOKEN");
      assertThat(claims.getIssuer()).isEqualTo(ISSUER);
      assertThat(claims.getAudience()).contains(AUDIENCE);
    }

    @Test
    void generateAccessToken_containsExpectedClaims() {
      String token = jwtService.generateAccessToken(testUser);

      Claims claims = jwtService.extractClaims(token);
      assertThat(claims.getSubject()).isEqualTo(testUser.getId().toString());
      assertThat(claims.get("tokenType", String.class)).isEqualTo("ACCESS_TOKEN");
      assertThat(claims.get("email", String.class)).isEqualTo(testUser.getEmail());
      assertThat(claims.get("role", String.class)).isEqualTo(testUser.getRole().toString());
    }

    @Test
    void generateTokens_returnsBothTokenTypes() {
      TokenDTO tokens = jwtService.generateTokens(testUser);

      assertThat(tokens.getRefreshToken()).isNotBlank();
      assertThat(tokens.getAccessToken()).isNotBlank();
      assertTrue(jwtService.isRefreshToken(tokens.getRefreshToken()));
      assertTrue(jwtService.isAccessToken(tokens.getAccessToken()));
    }
  }

  @Nested
  class TokenExtraction {

    @Test
    void extractClaims_invalidSignature_throws() {
      String token = jwtService.generateAccessToken(testUser);
      String tampered = token.substring(0, token.length() - 2) + "xx";

      assertThrows(Exception.class, () -> jwtService.extractClaims(tampered));
    }

    @Test
    void getExpiration_matchesExpectedWindow() {
      String token = jwtService.generateAccessToken(testUser);

      LocalDateTime expiration = jwtService.getExpiration(token);

      assertThat(expiration).isAfter(LocalDateTime.now().plusSeconds(ACCESS_TOKEN_EXPIRATION - 5));
      assertThat(expiration).isBefore(LocalDateTime.now().plusSeconds(ACCESS_TOKEN_EXPIRATION + 5));
    }

    @Test
    void extractSubject_returnsUserId() {
      String token = jwtService.generateAccessToken(testUser);
      assertThat(jwtService.extractSubject(token)).isEqualTo(testUser.getId().toString());
    }

    @Test
    void isRefreshToken_and_isAccessToken_areMutuallyExclusive() {
      String access = jwtService.generateAccessToken(testUser);
      String refresh = jwtService.generateRefreshToken(testUser);

      assertTrue(jwtService.isAccessToken(access));
      assertFalse(jwtService.isRefreshToken(access));

      assertTrue(jwtService.isRefreshToken(refresh));
      assertFalse(jwtService.isAccessToken(refresh));
    }
  }

  @Nested
  class RefreshTokenValidation {

    @Test
    void validateRefreshToken_nullToken_throwsIllegalArgument() {
      assertThrows(IllegalArgumentException.class, () -> jwtService.validateRefreshToken(null));
    }

    @Test
    void validateRefreshToken_blankToken_throwsIllegalArgument() {
      assertThrows(IllegalArgumentException.class, () -> jwtService.validateRefreshToken("  "));
    }

    @Test
    void validateRefreshToken_wrongTokenType_throwsInvalidToken() {
      String accessToken = jwtService.generateAccessToken(testUser);
      assertThrows(InValidTokenException.class, () -> jwtService.validateRefreshToken(accessToken));
    }

    @Test
    void validateRefreshToken_notActiveInRepo_throwsInvalidToken() {
      String refreshToken = jwtService.generateRefreshToken(testUser);
      when(activeRefreshTokenRepository.existsByRefreshToken(refreshToken)).thenReturn(false);

      assertThrows(InValidTokenException.class, () -> jwtService.validateRefreshToken(refreshToken));
    }

    @Test
    void validateRefreshToken_validAndActive_doesNotThrow() {
      String refreshToken = jwtService.generateRefreshToken(testUser);
      when(activeRefreshTokenRepository.existsByRefreshToken(refreshToken)).thenReturn(true);

      assertDoesNotThrow(() -> jwtService.validateRefreshToken(refreshToken));
    }
  }

  @Nested
  class AccessTokenValidation {

    @Test
    void validateAccessToken_nullToken_throwsIllegalArgument() {
      assertThrows(IllegalArgumentException.class, () -> jwtService.validateAccessToken(null));
    }

    @Test
    void validateAccessToken_wrongTokenType_throwsInvalidToken() {
      String refreshToken = jwtService.generateRefreshToken(testUser);
      assertThrows(InValidTokenException.class, () -> jwtService.validateAccessToken(refreshToken));
    }

    @Test
    void validateAccessToken_valid_doesNotThrow() {
      String accessToken = jwtService.generateAccessToken(testUser);
      assertDoesNotThrow(() -> jwtService.validateAccessToken(accessToken));
    }
  }

  @Nested
  class StoreActiveRefreshToken {

    @Test
    void storeActiveRefreshToken_nullUserId_throwsIllegalArgument() {
      String refreshToken = jwtService.generateRefreshToken(testUser);
      assertThrows(IllegalArgumentException.class,
          () -> jwtService.storeActiveRefreshToken(null, refreshToken));
      verifyNoInteractions(activeRefreshTokenRepository);
    }

    @Test
    void storeActiveRefreshToken_blankRefreshToken_throwsIllegalArgument() {
      assertThrows(IllegalArgumentException.class,
          () -> jwtService.storeActiveRefreshToken(testUser.getId(), " "));
      verifyNoInteractions(activeRefreshTokenRepository);
    }

    @Test
    void storeActiveRefreshToken_valid_savesEntityWithExpectedFields() {
      String refreshToken = jwtService.generateRefreshToken(testUser);

      jwtService.storeActiveRefreshToken(testUser.getId(), refreshToken);

      verify(activeRefreshTokenRepository).save(activeRefreshTokenArgumentCaptor.capture());

      ActiveRefreshToken saved = activeRefreshTokenArgumentCaptor.getValue();
      assertThat(saved.getUserId()).isEqualTo(testUser.getId());
      assertThat(saved.getRefreshToken()).isEqualTo(refreshToken);
      assertThat(saved.isRevoked()).isFalse();
      assertThat(saved.getExpiresAt()).isNotNull();
    }
  }

  @Nested
  class RemoveRedundantActiveRefreshTokens {

    @Test
    void removeRedundant_nullUserId_throwsIllegalArgument() {
      assertThrows(IllegalArgumentException.class,
          () -> jwtService.removeRedundantActiveRefreshTokens(null));
      verifyNoInteractions(activeRefreshTokenRepository);
    }

    @Test
    void removeRedundant_valid_deletesByUserId() {
      UUID userId = testUser.getId();
      jwtService.removeRedundantActiveRefreshTokens(userId);
      verify(activeRefreshTokenRepository).deleteByUserId(userId);
    }
  }

  @Nested
  class RevokeRefreshToken {

    @Test
    void revoke_nullToken_throwsIllegalArgument() {
      assertThrows(IllegalArgumentException.class, () -> jwtService.revokeRefreshToken(null));
    }

    @Test
    void revoke_notARefreshToken_throwsInvalidToken() {
      String accessToken = jwtService.generateAccessToken(testUser);
      assertThrows(InValidTokenException.class, () -> jwtService.revokeRefreshToken(accessToken));
    }

    @Test
    void revoke_notActive_throwsInvalidToken() {
      String refreshToken = jwtService.generateRefreshToken(testUser);
      when(activeRefreshTokenRepository.existsByRefreshToken(refreshToken)).thenReturn(false);

      assertThrows(InValidTokenException.class, () -> jwtService.revokeRefreshToken(refreshToken));
    }

    @Test
    void revoke_valid_deletesTokenAndCleansUpRedundantTokens() {
      String refreshToken = jwtService.generateRefreshToken(testUser);
      UUID userId = testUser.getId();
      when(activeRefreshTokenRepository.existsByRefreshToken(refreshToken)).thenReturn(true);

      jwtService.revokeRefreshToken(refreshToken);

      verify(activeRefreshTokenRepository).deleteByUserIdAndRefreshToken(userId, refreshToken);
      verify(activeRefreshTokenRepository).deleteByUserId(userId);
    }
  }

  @Nested
  class ExtractBearerToken {

    @Test
    void extractBearerToken_nullHeader_throwsEmptyToken() {
      assertThrows(EmptyTokenException.class, () -> jwtService.extractBearerToken(null));
    }

    @Test
    void extractBearerToken_missingBearerPrefix_throwsEmptyToken() {
      assertThrows(EmptyTokenException.class, () -> jwtService.extractBearerToken("Basic abc123"));
    }

    @Test
    void extractBearerToken_blankTokenAfterPrefix_throwsEmptyToken() {
      assertThrows(EmptyTokenException.class, () -> jwtService.extractBearerToken("Bearer "));
    }

    @Test
    void extractBearerToken_valid_returnsToken() {
      String result = jwtService.extractBearerToken("Bearer abc123.def456.ghi789");
      assertThat(result).isEqualTo("abc123.def456.ghi789");
    }
  }
}
