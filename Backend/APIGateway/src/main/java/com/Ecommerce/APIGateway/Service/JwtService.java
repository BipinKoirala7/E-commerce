package com.Ecommerce.APIGateway.Service;

import com.Ecommerce.APIGateway.Client.UserServiceClient;
import com.Ecommerce.APIGateway.DTOs.RestApiResponse;
import com.Ecommerce.APIGateway.DTOs.TokenDTO;
import com.Ecommerce.APIGateway.Exception.EmptyTokenException;
import com.Ecommerce.APIGateway.Exception.InValidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.el.parser.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

  private final CookieService cookieService;
  private final UserServiceClient userServiceClient;

  @Value("${app.applicationSignatureKey}")
  private String APPLICATION_SIGNATURE;

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

  public TokenDTO refreshToken(HttpServletRequest request, HttpServletResponse response) {
    log.debug("Refreshing Token...");

    String refreshToken = cookieService.extractRefreshTokenFromRequest(request);
    log.debug("Refreshing Token Info - Refresh Token found in cookie");

    validateRefreshToken(refreshToken);
    log.debug("Refreshing Token Info - Refresh Token is partially (need further checking with db) valid");

    RestApiResponse<TokenDTO> apiResponse = userServiceClient.refreshToken(refreshToken);

    if(!apiResponse.getSuccess()){
      log.debug("Refreshing Token Failed - Unable to refresh token: User Service token refresh failed");
      throw new InValidTokenException("Refreshing Token Failed");  // Maybe use something different
    }

    TokenDTO newTokenSet = apiResponse.getData();

    cookieService.deleteRefreshTokenCookie(response);
    cookieService.deleteAccessTokenCookie(response);
    log.debug("Refreshing Token Info - Old cookies deleted successfully");

    response.addCookie(cookieService.createRefreshTokenCookie(newTokenSet.getRefreshToken()));
    response.addCookie(cookieService.createAccessTokenCookie(newTokenSet.getAccessToken()));
    log.debug("Refreshing Token Info - New Cookies for token created successfully");
    return newTokenSet;
  }

}