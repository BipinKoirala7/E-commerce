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
import jakarta.validation.constraints.NotNull;
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
  public void registerUser(@NotNull UserCreateDTO userCreateDTO) {
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
    log.info("Login User...");

    if (Objects.isNull(userLoginDTO)) {
      log.debug("Login User Failed - User Login DTO is null");
      throw new IllegalArgumentException("Login Credentials must be provided");
    }

    User user = userService.getUserByEmail(userLoginDTO.getEmail());
    log.debug("Login User Info - Getting User of Given Email");

    if (Objects.isNull(user)) {
      log.debug("Login User Failed -User with given email is not present");
      throw new UserNotFoundException("User doesn't exist");
    }

    if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
      log.debug("Login User Failed - Passwords don't match");
      throw new IncorrectEmailOrPasswordException("Email or Password is incorrect");
    }

    TokenDTO tokenSet = jwtService.generateTokens(user);
    log.debug("Login User Info - Generating Tokens for User");

    response.addCookie(cookieService.createRefreshTokenCookie(tokenSet.getRefreshToken()));
    response.addCookie(cookieService.createAccessTokenCookie(tokenSet.getAccessToken()));

    jwtService.storeActiveRefreshToken(user.getId(), tokenSet.getRefreshToken());
    log.debug("Login User Info - Active Refresh Token Stored");

    userService.updateUserLastLoginAt(user.getId());
    log.debug("Login User Info - User's last login time updated");

    log.info("Login User Success");
  }

  @Transactional
  public void loginOAuthUser(String email, String providerId, HttpServletResponse response) {
    log.info("OAuth User Login...");

    if (Objects.isNull(email)) {
      log.debug("OAuth User Login Failed - Email is null");
      throw new IllegalArgumentException("Please Provide credentials properly.");
    }

    if (Objects.isNull(providerId)) {
      log.debug("OAuth User Login Failed - Provider Id is null");
      throw new IllegalArgumentException("Please Provide credentials properly.");
    }

    User user = userService.getUserByEmail(email);
    log.debug("OAuth User Login Info - Getting User of Given Email");

    if (Objects.isNull(user)) {
      log.debug("OAuth User Login Failed - User with given social email is not present");
      throw new UserNotFoundException("User doesn't exist");
    }

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

  //  This is used by API Gateway for refreshing tokens.
  @Transactional
  public String refreshTokens(String refreshToken) {
    log.debug("Refreshing tokens...");

    jwtService.validateRefreshToken(refreshToken);
    log.debug("Refreshing Tokens Info - Refresh Token is valid");

    UUID userId = UUID.fromString(jwtService.extractSubject(refreshToken));
    log.debug("Refreshing Tokens Info - User Id extracted from Refresh Token");

    User user = userService.getUserById(userId);
    String accessToken = jwtService.generateAccessToken(user);
    log.debug("Refreshing Tokens Info - Tokens Generated Successfully");

    log.debug("Refreshing Tokens Success");
    return accessToken;
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
