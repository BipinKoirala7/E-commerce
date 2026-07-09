package com.Ecommerce.UserService.Filters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;

import com.Ecommerce.UserService.Security.JwtAuthenticationToken;
import com.Ecommerce.UserService.Service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

  private static final String AUTH_HEADER_KEY = "Authorization";
  private static final String AUTH_HEADER_VALUE = "Bearer some-bearer-token";
  private static final String BEARER_TOKEN = "some-bearer-token";

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @Mock
  private JwtService jwtService;

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Nested
  class ShouldNotFilter {

    private JwtFilter underTest;

    @BeforeEach
    void setUp() {
      underTest = new JwtFilter(new AntPathMatcher(), jwtService);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "/internal/auth/token-refresh",
        "/auth/register",
        "/auth/login",
        "/auth/logout",
        "/auth/oauth/google",
        "/login/oauth2/code/google",   // matches /login/oauth2/code/**
        "/oauth2/authorization/github", // matches /oauth2/authorization/**
        "/internal/auth/token/refresh", // matches /internal/auth/token/**
        "/actuator/health"              // matches /actuator/**
    })
    void shouldSkipFilter_forExcludedPaths(String path) {
      when(request.getServletPath()).thenReturn(path);

      boolean result = underTest.shouldNotFilter(request);

      assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "/users/me",
        "/orders",
        "/auth/refresh-token",
        "/"
    })
    void shouldNotSkipFilter_forProtectedPaths(String path) {
      when(request.getServletPath()).thenReturn(path);

      boolean result = underTest.shouldNotFilter(request);

      assertThat(result).isFalse();
    }
  }

  @Nested
  class DoFilterInternal {

    private JwtFilter underTest;

    @BeforeEach
    void setUp() {
      underTest = new JwtFilter(new AntPathMatcher(), jwtService);
    }

    @Test
    void shouldAuthenticateAndContinueChain_whenTokenIsValid() throws ServletException, IOException {
      UUID userId = UUID.randomUUID();
      when(request.getHeader(AUTH_HEADER_KEY)).thenReturn(AUTH_HEADER_VALUE);
      when(jwtService.extractBearerToken(AUTH_HEADER_VALUE)).thenReturn(BEARER_TOKEN);
      when(jwtService.extractSubject(BEARER_TOKEN)).thenReturn(userId.toString());

      underTest.doFilterInternal(request, response, filterChain);

      verify(jwtService).validateAccessToken(BEARER_TOKEN);
      verify(filterChain).doFilter(request, response);

      JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext()
          .getAuthentication();

      assertNotNull(authenticationToken);
      assertEquals(BEARER_TOKEN, authenticationToken.getAccessToken());
      assertEquals(userId, authenticationToken.getUserId());
      assertTrue(authenticationToken.isAuthenticated());

      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      assertThat(authentication).isInstanceOf(JwtAuthenticationToken.class);
      assertThat(authentication.isAuthenticated()).isTrue();
      assertThat(authentication.getAuthorities())
          .extracting(Object::toString)
          .containsExactly("ROLE_USER");
    }

    @Test
    void shouldValidateBeforeExtractingSubject() throws ServletException, IOException {
      // Order matters: validating an expired/tampered token before trusting its
      // subject claim is the whole point of calling validateAccessToken first.
      when(request.getHeader(AUTH_HEADER_KEY)).thenReturn(AUTH_HEADER_VALUE);
      when(jwtService.extractBearerToken(AUTH_HEADER_VALUE)).thenReturn(BEARER_TOKEN);
      when(jwtService.extractSubject(BEARER_TOKEN)).thenReturn(UUID.randomUUID().toString());

      underTest.doFilterInternal(request, response, filterChain);

      InOrder inOrder = Mockito.inOrder(jwtService);
      inOrder.verify(jwtService).validateAccessToken(BEARER_TOKEN);
      inOrder.verify(jwtService).extractSubject(BEARER_TOKEN);
    }

    @Test
    void shouldNotContinueChain_whenAuthorizationHeaderIsMissing() throws ServletException, IOException {
      when(request.getHeader(AUTH_HEADER_KEY)).thenReturn(null);
      when(jwtService.extractBearerToken(isNull()))
          .thenThrow(new IllegalArgumentException("Missing Authorization header"));

      assertThrows(IllegalArgumentException.class,
          () -> underTest.doFilterInternal(request, response, filterChain));

      verify(jwtService, never()).validateAccessToken(any());
      verify(jwtService, never()).extractSubject(any());
      verify(filterChain, never()).doFilter(any(), any());
      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldNotContinueChain_whenTokenIsInvalidOrExpired() throws ServletException, IOException {
      when(request.getHeader(AUTH_HEADER_KEY)).thenReturn(AUTH_HEADER_VALUE);
      when(jwtService.extractBearerToken(AUTH_HEADER_VALUE)).thenReturn(BEARER_TOKEN);
      doThrow(new RuntimeException("Invalid or expired token"))
          .when(jwtService).validateAccessToken(BEARER_TOKEN);
      // throws jwt exceptions like ExpiredJwtException, MalformedJwtException, etc. which are all RuntimeExceptions

      assertThrows(RuntimeException.class,
          () -> underTest.doFilterInternal(request, response, filterChain));

      verify(jwtService, never()).extractSubject(any());
      verify(filterChain, never()).doFilter(any(), any());
      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldNotContinueChain_whenSubjectIsNotAValidUuid() throws ServletException, IOException {
      when(request.getHeader(AUTH_HEADER_KEY)).thenReturn(AUTH_HEADER_VALUE);
      when(jwtService.extractBearerToken(AUTH_HEADER_VALUE)).thenReturn(BEARER_TOKEN);
      when(jwtService.extractSubject(BEARER_TOKEN)).thenReturn("not-a-valid-uuid");

      assertThrows(IllegalArgumentException.class,
          () -> underTest.doFilterInternal(request, response, filterChain));

      verify(filterChain, never()).doFilter(any(), any());
      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
  }
}