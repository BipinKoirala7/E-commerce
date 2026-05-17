package com.Ecommerce.UserService.Service;

import com.Ecommerce.UserService.DTOs.Request.OAuthUserCreateDTO;
import com.Ecommerce.UserService.DTOs.Request.UserCreateDTO;
import com.Ecommerce.UserService.DTOs.Request.UserLoginDTO;
import com.Ecommerce.UserService.DTOs.Response.TokenDTO;
import com.Ecommerce.UserService.Exception.*;
import com.Ecommerce.UserService.Model.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.Token;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Core authentication class for UserService. Handles User registration
 * login, token refresh, and logout.
 *
 * @see JwtService
 * @see UserService
 * @see CookieService
 * @see UserCreateDTO
 * @see OAuthUserCreateDTO
 * @see UserLoginDTO
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final JwtService jwtService;
  private final UserService userService;
  private final CookieService cookieService;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public void registerUser(UserCreateDTO userCreateDTO) {
    log.debug("Registering User...");
    userService.createNewUser(userCreateDTO);
    log.debug("Successfully Registered User!");
  }

  @Transactional
  public User registerOAuthUser(OAuthUserCreateDTO oAuthUserCreateDTO) {
    log.debug("OAuth User Registering Process...");
    User user = userService.createNewOAuthUser(oAuthUserCreateDTO);
    log.debug("Successfully Registered OAuth User!");
    return user;
  }

  @Transactional
  public void loginUser(UserLoginDTO userLoginDTO, HttpServletResponse response) {
    log.info("User Login...");

    if (Objects.isNull(userLoginDTO)) {
      log.debug("User Login Failed - User Login DTO is null");
      throw new IllegalArgumentException("Login Credentials must be provided");
    }
    log.debug("User Login Info - User Login DTO is present");

    User user = userService.getUserByEmail(userLoginDTO.getEmail());
    log.debug("User Login Info - Getting User of Given Email");

    if (Objects.isNull(user)) {
      log.debug("User Login Failed -User with given email is not present");
      throw new UserNotFoundException("User doesn't exist");
    }
    log.debug("User Login Info - User with given email exists");

    if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
      log.debug("User Login Failed - Passwords don't match");
      throw new IncorrectEmailOrPasswordException("Email or Password is incorrect");
    }
    log.debug("User Login Info - Passwords match");

    TokenDTO tokenSet = jwtService.generateTokens(user);
    log.debug("User Login Info - Generating Tokens for User");

    response.addCookie(cookieService.createRefreshTokenCookie(tokenSet.getRefreshToken()));
    response.addCookie(cookieService.createAccessTokenCookie(tokenSet.getAccessToken()));

    jwtService.storeActiveRefreshToken(user.getId(), tokenSet.getRefreshToken());
    log.debug("User Login Info - Active Refresh Token Stored");

    log.debug("User Login Info - Updating user's last login time");
    userService.updateUserLastLoginAt(user.getId());

    log.info("User Login Success");
  }

  @Transactional
  public void loginOAuthUser(String email, String providerId, HttpServletResponse response) {
    log.info("OAuth User Login...");

    if (Objects.isNull(email)) {
      log.debug("OAuth User Login Failed - Email is null");
      throw new IllegalArgumentException("Please Provide credentials properly.");
    }
    log.debug("OAuth User Login Info - Email is present");

    if (Objects.isNull(providerId)) {
      log.debug("OAuth User Login Failed - Provider Id is null");
      throw new IllegalArgumentException("Please Provide credentials properly.");
    }
    log.debug("OAuth User Login Info - Provider Id is present");

    User user = userService.getUserByEmail(email);
    log.debug("OAuth User Login Info - Getting User of Given Email");

    if (Objects.isNull(user)) {
      log.debug("OAuth User Login Failed - User with given social email is not present");
      throw new UserNotFoundException("User doesn't exist");
    }
    log.debug("OAuth User Login Info - User with given social email exists");

    TokenDTO tokenSet = jwtService.generateTokens(user);
    log.debug("OAuth User Login Info - Tokens Generated");

    response.addCookie(cookieService.createRefreshTokenCookie(tokenSet.getRefreshToken()));
    response.addCookie(cookieService.createAccessTokenCookie(tokenSet.getAccessToken()));

    jwtService.storeActiveRefreshToken(user.getId(), tokenSet.getRefreshToken());
    log.debug("OAuth User Login Info - Active Refresh Token Stored");

    userService.updateUserLastLoginAt(user.getId());
    log.debug("OAuth User Login Info - Last Login Time Updated");

    log.info("OAuth User Login Success");
  }

  //  This is for the login and sign Up to create refresh tokens
//  @Transactional
//  public void refreshTokens(HttpServletRequest request, HttpServletResponse response) {
//    log.info("Refreshing tokens...");
//
//    Cookie refreshTokenCookie = cookieService.getCookie(request, cookieService.getREFRESH_TOKEN());
//    String refreshToken = Optional
//        .ofNullable(refreshTokenCookie)
//        .map(Cookie::getValue)
//        .orElseThrow(() -> {
//          log.warn("Refreshing Tokens Failed - Refresh Token not found");
//          return new EmptyTokenException("Refresh Token is empty");
//        });
//    log.debug("Refreshing Tokens Info - Refresh Token found in cookie");
//
//    jwtService.validateRefreshToken(refreshToken);
//    log.debug("Refreshing Tokens Info - Refresh Token is valid");
//
//    UUID userId = UUID.fromString(jwtService.extractSubject(refreshToken));
//    log.debug("Refreshing Tokens Info - User Id extracted from Refresh Token");
//
//    User user = userService.getUserById(userId);
//    TokenDTO tokens = jwtService.generateTokens(user);
//    log.debug("Refreshing Tokens Info - Tokens Generated Successfully");
//
//    cookieService.deleteRefreshTokenCookie(response);
//    cookieService.deleteAccessTokenCookie(response);
//    log.debug("Refreshing Tokens Info - Old cookies deleted successfully");
//
//    jwtService.revokeRefreshToken(refreshToken);
//    log.debug("Refreshing Tokens Info - Refresh Token Revoked");
//
//    jwtService.storeActiveRefreshToken(user.getId(), tokens.getRefreshToken());
//    log.debug("Refreshing Tokens Info - Active Refresh Token Stored");
//
//    response.addCookie(cookieService.createRefreshTokenCookie(tokens.getRefreshToken()));
//    response.addCookie(cookieService.createAccessTokenCookie(tokens.getAccessToken()));
//    log.debug("Refreshing Tokens Info - New cookies created successfully");
//
//    log.debug("Refreshing Tokens Success");
//  }

  //  This is used by API Gateway for refreshing tokens.
  @Transactional
  public TokenDTO refreshTokens(String refreshToken) {
    log.info("Refreshing tokens ..");

    jwtService.validateRefreshToken(refreshToken);
    log.debug("Refreshing Tokens Info - Refresh Token is valid");

    UUID userId = UUID.fromString(jwtService.extractSubject(refreshToken));
    log.debug("Refreshing Tokens Info - User Id extracted from Refresh Token");

    User user = userService.getUserById(userId);
    TokenDTO tokens = jwtService.generateTokens(user);
    log.debug("Refreshing Tokens Info - Tokens Generated Successfully");

    jwtService.revokeRefreshToken(refreshToken);
    log.debug("Refreshing Tokens Info - Refresh Token Revoked");

    jwtService.storeActiveRefreshToken(user.getId(), tokens.getRefreshToken());
    log.debug("Refreshing Tokens Info - Active Refresh Token Stored");

    log.debug("Refreshing Tokens Success");
    return tokens;
  }

  @Transactional
  public void logout(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
    log.info("Logging out User...");
    String refreshToken = Optional.ofNullable(cookieService.getCookie(request, cookieService.getREFRESH_TOKEN())).map(Cookie::getValue)
        .orElseThrow(() -> {
          log.debug("Logging out User Info - Refresh Token not found in cookie");
          return new EmptyTokenException("Refresh Token is empty");
        });
    log.debug("Logging out User Info - Refresh Token found in cookie");

    jwtService.validateRefreshToken(refreshToken);
    log.debug("Logging out User Info - Refresh Token is valid");

    jwtService.revokeRefreshToken(refreshToken);
    log.debug("Logging Out User Info - Refresh Token Revoked");

    cookieService.deleteAccessTokenCookie(response);
    cookieService.deleteRefreshTokenCookie(response);
    log.debug("Logging out User Success - Cookies deleted successfully");

    log.info("Logging Out User Success");
  }
}
