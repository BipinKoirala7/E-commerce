package com.Ecommerce.UserService.Service;

import com.Ecommerce.UserService.DTOs.Response.TokenDTO;
import com.Ecommerce.UserService.Exception.EmptyTokenException;
import com.Ecommerce.UserService.Exception.InValidTokenException;
import com.Ecommerce.UserService.Model.ActiveRefreshToken;
import com.Ecommerce.UserService.Model.User;
import com.Ecommerce.UserService.Repository.ActiveRefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * JWT Service for User Service. Handles token creation,
 * extract information from token, validate different tokens
 * store active token in database.
 *
 * @see ActiveRefreshTokenRepository
 * @see ActiveRefreshToken
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

  private final ActiveRefreshTokenRepository activeRefreshTokenRepository;
  @Value("${app.applicationSignatureKey}")
  private String APPLICATION_SIGNATURE;

  @Value("${app.accessTokenExpiration:3600}")
  private long accessTokenExpiration;

  @Value("${app.refreshTokenExpiration:259200}")
  private long refreshTokenExpiration;

  @Value("${app.jwt.audience:ecommerce.com}")
  private String AUDIENCE;

  @Value("${app.jwt.issuer:api.ecommerce.com}")
  private String ISSUER;

  private final String REFRESH_TOKEN = "REFRESH_TOKEN";
  private final String ACCESS_TOKEN = "ACCESS_TOKEN";
  private final String ACCESS_TOKEN_TYPE = "tokenType";

  public SecretKey generateSignInKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(APPLICATION_SIGNATURE));
  }

  public String generateRefreshToken(@NonNull User user) {
    return Jwts.builder()
        .signWith(generateSignInKey())
        .issuer(ISSUER)
        .audience().add(AUDIENCE).and()
        .subject(String.valueOf(user.getId()))
        .claim("tokenType", REFRESH_TOKEN)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration * 1000L))
        .compact();
  }

  public String generateAccessToken(@NonNull User user) {
    return Jwts.builder()
        .signWith(generateSignInKey())
        .issuer(ISSUER)
        .audience().add(AUDIENCE).and()
        .subject(String.valueOf(user.getId()))
        .claim("tokenType", ACCESS_TOKEN)
        .claim("email", user.getEmail())
        .claim("role", user.getRole().toString())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration * 1000L))
        .compact();
  }

  public Claims extractClaims(@NonNull String token) {
    return Jwts
        .parser()
        .verifyWith(generateSignInKey())
        .requireIssuer(ISSUER)
        .requireAudience(AUDIENCE)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public LocalDateTime getExpiration(@NonNull String token) {
    return extractClaims(token).getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  public String extractSubject(String token) {
    return extractClaims(token).getSubject();
  }

  public String extractTokenType(String token) {
    return extractClaims(token).get(ACCESS_TOKEN_TYPE, String.class);
  }

  public boolean isRefreshToken(String token) {
    return REFRESH_TOKEN.equals(extractTokenType(token));
  }

  public boolean isAccessToken(String token) {
    return ACCESS_TOKEN.equals(extractTokenType(token));
  }


  public void validateRefreshToken(String token) {
    log.debug("Refresh Token Validation...");

    if (Objects.isNull(token) || token.isBlank()) {
      log.debug("Refresh Token Validation Failed - Token is null");
      throw new IllegalArgumentException("Token must be provided");
    }
    log.debug("Refresh Token Validation Info - Refresh Token is present");

    if (!isRefreshTokenActive(token)) {
      log.debug("Refresh Token Validation Failed - Token is not active");
      throw new InValidTokenException("Given Token is not active");
    }
    log.debug("Refresh Token Validation Info - Token is active");

    if (!isRefreshToken(token)) {
      log.debug("Refresh Token Validation Failed - Given token is not a refresh token");
      throw new InValidTokenException("Given token is not a refresh token");
    }
    log.debug("Refresh Token Validation Success");
  }

  public void validateAccessToken(String token) {
    log.debug("Access Token Validation...");

    if (Objects.isNull(token) || token.isBlank()) {
      log.debug("Access Token Validation Failed - Token is null");
      throw new IllegalArgumentException("Token must be provided");
    }
    log.debug("Access Token Validation Info - Token is present");

    if (!isAccessToken(token)) {
      log.debug("Access Token Validation Failed - Given token is not a access token");
      throw new InValidTokenException("Given token is not a access token");
    }
    log.debug("Access Token Validation Success");
  }

  @Transactional
  public void storeActiveRefreshToken(UUID userId, String refreshToken) {
    log.debug("Storing Active Refresh Token");

    if (Objects.isNull(userId) || userId.toString().isBlank()) {
      log.warn("Storing Active Refresh Token Failed - User Id is missing");
      throw new IllegalArgumentException("User Id is missing");
    }
    log.debug("Storing Active Refresh Token Info - User Id is present");

    if (Objects.isNull(refreshToken) || refreshToken.isBlank()) {
      log.warn("Storing Active Refresh Token Failed - Refresh Token is missing");
      throw new IllegalArgumentException("Refresh Token is missing");
    }
    log.debug("Storing Active Refresh Token Info - Refresh Token is present");

    ActiveRefreshToken activeRefreshToken = new ActiveRefreshToken();
    activeRefreshToken.setUserId(userId);
    activeRefreshToken.setRefreshToken(refreshToken);
    activeRefreshToken.setExpiresAt(getExpiration(refreshToken));
    activeRefreshToken.setRevoked(false);
    log.debug("Storing Active Refresh Token Info - Successfully created Active Refresh Token entity");

    activeRefreshTokenRepository.save(activeRefreshToken);
    log.debug("Successfully stored Refresh Token");
  }

  @Transactional
  public void removeRedundantActiveRefreshTokens(UUID userId) {
    log.debug("Removing Redundant Active Refresh Tokens...");

    if (Objects.isNull(userId) || userId.toString().isBlank()) {
      log.warn("Removing Redundant Active Refresh Tokens Failed - User Id is missing");
      throw new IllegalArgumentException("User Id is missing");
    }
    log.debug("Removing Redundant Active Refresh Tokens Info - User Id is present");

    activeRefreshTokenRepository.deleteByUserId(userId);
    log.debug("Successfully removed redundant active refresh tokens for user");
  }

  @Transactional
  public void revokeRefreshToken(String refreshToken) {
    log.debug("Revoking Refresh Token...");

    if (Objects.isNull(refreshToken)) {
      log.info("Revoking Refresh Token Failed - Refresh Token is missing");
      throw new IllegalArgumentException("Token is missing");
    }
    log.debug("Revoking Refresh Token Info - Refresh Token is present");

    if (!isRefreshToken(refreshToken)) {
      log.debug("Revoking Refresh Token Info - Refresh Token is invalid");
      throw new InValidTokenException("Token is invalid");
    }
    log.debug("Revoking Refresh Token Info - Refresh Token is valid");

    if (!isRefreshTokenActive(refreshToken)) {
      log.debug("Revoking Refresh Token Info - Refresh Token is not active");
      throw new InValidTokenException("Token is invalid");
    }
    log.debug("Revoking Refresh Token Info - Refresh Token is active, proceeding to revoke");

    activeRefreshTokenRepository
        .deleteByUserIdAndRefreshToken(UUID.fromString(extractSubject(refreshToken)), refreshToken);
    removeRedundantActiveRefreshTokens(UUID.fromString(extractSubject(refreshToken)));
    log.debug("Revoking Refresh Token Info - Successfully deleted Active Refresh Token");

    log.debug("Revoking Refresh Token Success");
  }

  public TokenDTO generateTokens(User user) {
    log.debug("Generating Tokens...");
    String refreshToken = generateRefreshToken(user);
    String accessToken = generateAccessToken(user);

    log.debug("Successfully generated Tokens...");
    return new TokenDTO(refreshToken, accessToken);
  }

  public boolean isRefreshTokenActive(String refreshToken) {
    return activeRefreshTokenRepository.existsByRefreshToken(refreshToken);
  }

  public String extractBearerToken(String authHeader) {
    log.debug("Extracting Bearer Token...");

    if (Objects.isNull(authHeader) || !authHeader.startsWith("Bearer")) {
      log.debug("Extracting Bearer Token Failed - Header is missing or not a Bearer Token");
      throw new EmptyTokenException("Invalid Authorization header");
    }
    log.debug("Extracting Bearer Token Info - Authorization header is present");

    String token = authHeader.substring(7);

    if (token.isBlank()) {
      throw new EmptyTokenException("Token is blank or empty");
    }
    log.debug("Extracting Bearer Token Info - Token is present");

    log.debug("Extracting Bearer Token Success");
    return token;
  }
}
