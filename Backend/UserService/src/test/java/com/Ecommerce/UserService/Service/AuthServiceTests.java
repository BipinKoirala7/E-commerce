package com.Ecommerce.UserService.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.Ecommerce.UserService.DTOs.Request.UserLoginDTO;
import com.Ecommerce.UserService.DTOs.Response.TokenDTO;
import com.Ecommerce.UserService.Exception.IncorrectEmailOrPasswordException;
import com.Ecommerce.UserService.Exception.UserNotFoundException;
import com.Ecommerce.UserService.Model.User;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

  @InjectMocks
  private AuthService underTest;

  @Mock
  private JwtService jwtService;

  @Mock
  private UserService userService;

  @Mock
  private CookieService cookieService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private HttpServletResponse response;

  @Captor
  ArgumentCaptor<String> refreshTokenCaptor;

  @Captor
  ArgumentCaptor<UUID> userIdCaptor;

  private UserLoginDTO userLoginDTO;
  private User user;
  private TokenDTO tokenDTO;
  private Cookie refreshTokenCookie;
  private Cookie accessTokenCookie;
  private String refreshToken = "New Refresh Token";
  private String accessToken = "New Access Token";
  private final UUID userId = UUID.randomUUID();

  @BeforeEach
  void beforeEach() {
    userLoginDTO = new UserLoginDTO();
    userLoginDTO.setEmail("bipin.koirala.123@gmail.com");
    userLoginDTO.setPassword("bipin.password");

    user = new User();
    user.setId(userId);
    user.setEmail(userLoginDTO.getEmail());
    user.setPassword("Encoded Password");

    tokenDTO = new TokenDTO(refreshToken, accessToken);

    refreshTokenCookie = new Cookie("REFRESH_TOKEN", tokenDTO.getRefreshToken());
    accessTokenCookie = new Cookie("ACCESS_TOKEN", tokenDTO.getAccessToken());
  }

  @Test
  void loginUser() {
    when(userService.getUserByEmail(userLoginDTO.getEmail())).thenReturn(user);
    when(passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())).thenReturn(true);
    when(jwtService.generateTokens(user)).thenReturn(tokenDTO);
    when(cookieService.createRefreshTokenCookie(tokenDTO.getRefreshToken())).thenReturn(refreshTokenCookie);
    when(cookieService.createAccessTokenCookie(tokenDTO.getAccessToken())).thenReturn(accessTokenCookie);

    underTest.loginUser(userLoginDTO, response);

    verify(jwtService).storeActiveRefreshToken(eq(user.getId()), refreshTokenCaptor.capture());
    assertEquals(refreshToken, refreshTokenCaptor.getValue());

    verify(userService).getUserByEmail(userLoginDTO.getEmail());
    verify(passwordEncoder).matches(userLoginDTO.getPassword(), user.getPassword());
    verify(jwtService).generateTokens(user);
    verify(cookieService).createRefreshTokenCookie(tokenDTO.getRefreshToken());
    verify(cookieService).createAccessTokenCookie(tokenDTO.getAccessToken());
  }
  // Check if we have to test for creating a Token Dto without any tokens inside

  @Test
  void loginUser_whenLoginDtoIsNUll() {
    userLoginDTO = null;

    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
        () -> underTest.loginUser(userLoginDTO, response));

    assertEquals("Login Credentials must be provided", e.getMessage());
  }

  @Test
  void loginUser_whenUserWithEmailIsDoesNotExists() {
    when(userService.getUserByEmail(userLoginDTO.getEmail())).thenReturn(null);

    UserNotFoundException e = assertThrows(UserNotFoundException.class,
        () -> underTest.loginUser(userLoginDTO, response));
    assertEquals("User doesn't exist", e.getMessage());
  }

  @Test
  void loginUser_whenEmailIsCorrectButPasswordIsNot() {
    when(userService.getUserByEmail(userLoginDTO.getEmail())).thenReturn(user);
    when(passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())).thenReturn(false);

    IncorrectEmailOrPasswordException e = assertThrows(IncorrectEmailOrPasswordException.class,
        () -> underTest.loginUser(userLoginDTO, response));
    assertEquals("Email or Password is incorrect", e.getMessage());
  }

  @Test
  void refreshTokens() {
    String newAccessToken = "Again! New Access Token";
    when(jwtService.extractSubject(refreshToken)).thenReturn(userId.toString());
    when(userService.getUserById(userId)).thenReturn(user);
    when(jwtService.generateAccessToken(user)).thenReturn(newAccessToken);

    String result = underTest.refreshTokens(refreshToken);

    assertEquals(newAccessToken, result);

    verify(jwtService).extractSubject(refreshToken);
    verify(userService).getUserById(userId);
    verify(jwtService).generateAccessToken(user);
  }

  @Test
  void refreshTokens_whenRefreshTokenIsNull() {
    doThrow(new IllegalArgumentException("Refresh token cannot be null")).when(jwtService).extractSubject(null);

    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
        () -> underTest.refreshTokens(null));
    assertEquals("cannot be null", e.getMessage());
  }
}
