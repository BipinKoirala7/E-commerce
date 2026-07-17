package com.Ecommerce.OrderService.Integration;

import com.Ecommerce.OrderService.Service.JwtService;
import io.jsonwebtoken.Jwts;

import java.util.Date;
import java.util.UUID;

/**
 * Plain (non-Spring-managed) test utility for minting access tokens.
 * Intentionally NOT a Spring bean — it borrows the real, Spring-managed
 * {@link JwtService} for signing so it gets the actual injected
 * applicationSignatureKey instead of trying (and failing) to get its
 * own @Value fields populated outside the Spring context.
 */
public class TokenGenerator {

  private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

  private final JwtService jwtService;
  private final String audience;
  private final String issuer;

  public TokenGenerator(JwtService jwtService, String audience, String issuer) {
    this.jwtService = jwtService;
    this.audience = audience;
    this.issuer = issuer;
  }

  public String generateAccessToken(UUID userId, String email, String role) {
    long accessTokenExpiration = 3600;
    return Jwts.builder()
        .signWith(jwtService.generateSignInKey())
        .issuer(issuer)
        .audience().add(audience).and()
        .subject(String.valueOf(userId))
        .claim("tokenType", ACCESS_TOKEN)
        .claim("email", email)
        .claim("role", role)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration * 1000L))
        .compact();
  }
}