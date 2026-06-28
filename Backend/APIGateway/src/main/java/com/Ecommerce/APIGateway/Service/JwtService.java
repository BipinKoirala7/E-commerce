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
    log.info("Refresh Token Validation...");

    if (Objects.isNull(token) || token.isBlank()) {
      log.debug("Refresh Token Validation Failed - Token is null");
      throw new EmptyTokenException("Token must be provided");
    }
    if (!isRefreshToken(token)) {
      log.debug("Refresh Token Validation Failed - Given token is not a refresh token");
      throw new InValidTokenException("Given token is not a refresh token");
    }
    log.info("Refresh Token Validation Success");
  }

  public void validateAccessToken(String token) {
    log.info("Access Token Validation...");

    if (Objects.isNull(token) || token.isBlank()) {
      log.debug("Access Token Validation Failed - Token is null");
      throw new EmptyTokenException("Token must be provided");
    }
    if (!isAccessToken(token)) {
      log.debug("Access Token Validation Failed - Given token is not a access token");
      throw new InValidTokenException("Given token is not a access token");
    }
    log.info("Access Token Validation Success");
  }

  public String generateNewAccessToken(HttpServletRequest request, HttpServletResponse response) {
    log.info("Generate New Access Token...");

    String refreshToken = cookieService.extractRefreshTokenFromRequest(request);
    validateRefreshToken(refreshToken);
    log.debug("Refreshing Token Info - Refresh Token is partially valid");

    RestApiResponse<String> apiResponse = userServiceClient.refreshToken(refreshToken);

    if(!apiResponse.getSuccess()){
      log.debug("Refreshing Token Failed - Unable to refresh token");
      throw new InValidTokenException("Refreshing Token Failed");
    }

    cookieService.deleteAccessTokenCookie(response);
    response.addCookie(cookieService.createAccessTokenCookie(apiResponse.getData()));
    log.debug("Generate New Access Token Info - Old Access Token cookie deleted and new tokens generated");

    log.info("Generate New Access Token Success");

    return apiResponse.getData();
  }

}