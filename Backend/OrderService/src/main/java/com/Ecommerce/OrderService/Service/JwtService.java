package com.Ecommerce.OrderService.Service;

import com.Ecommerce.OrderService.Exception.EmptyTokenException;
import com.Ecommerce.OrderService.Exception.InValidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Objects;

/**
 * Handles jwt token validation
 * */
@Slf4j
@Service
public class JwtService {

  @Value("${app.applicationSignatureKey}")
  private String APPLICATION_SIGNATURE;

  @Value("${app.jwt.audience:ecommerce.com}")
  private String AUDIENCE;

  @Value("${app.jwt.issuer:api.ecommerce.com}")
  private String ISSUER;

  private final String ACCESS_TOKEN = "ACCESS_TOKEN";
  private final String ACCESS_TOKEN_TYPE = "tokenType";
  private final String ACCESS_TOKEN_EMAIL = "email";

  public SecretKey generateSignInKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(APPLICATION_SIGNATURE));
  }

  public Claims extractClaims(String token) {
    return Jwts
        .parser()
        .verifyWith(generateSignInKey())
        .requireIssuer(ISSUER)
        .requireAudience(AUDIENCE)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public String extractSubject(String token) {
    return extractClaims(token).getSubject();
  }

  public String extractEmail(String token) {
    return extractClaims(token).get(ACCESS_TOKEN_EMAIL, String.class);
  }

  public String extractTokenType(String token) {
    return extractClaims(token).get(ACCESS_TOKEN_TYPE, String.class);
  }

  public boolean isAccessToken(String token) {
    return ACCESS_TOKEN.equals(extractTokenType(token));
  }

  public void validateAccessToken(String token) {
    log.debug("Access Token Validation...");

    if (Objects.isNull(token)) {
      log.debug("Access Token Validation Failed - Token is null");
      throw new IllegalArgumentException("Token must be provided");
    }
    log.debug("Access Token Validation Info - Token is present");

    if (!isAccessToken(token)) {
      log.debug("Access Token Validation Failed - Given token is not a refresh token");
      throw new InValidTokenException("Token is invalid");
    }
    log.debug("Access Token Validation Success");
  }

  public String extractBearerToken(String authHeader) {
    log.debug("Extracting Bearer Token...");

    log.debug("Extracting Bearer Token Info - Checking if Authorization header is null");
    if (Objects.isNull(authHeader) || !authHeader.startsWith("Bearer")) {
      log.debug("Extracting Bearer Token Failed - Header is missing or not a Bearer Token");
      throw new EmptyTokenException("Invalid Authorization header");
    }
    log.debug("Extracting Bearer Token Info - Removing 'Bearer ' from Authorization header");
    String token = authHeader.substring(7);

    log.debug("Extracting Bearer Token Info - Checking if extracted token is blank");
    if (token.isBlank()) {
      throw new EmptyTokenException("Token is blank or empty");
    }

    log.debug("Extracting Bearer Token Success");
    return token;
  }
}
